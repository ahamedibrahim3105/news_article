package com.example.articles.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.articles.adapters.AdapterArticles
import com.example.articles.databinding.ActivityMainBinding
import com.example.articles.model.Article
import com.example.articles.model.ArticleModel
import com.example.articles.model.Source
import com.example.articles.room.AtriclesDatabase
import com.example.articles.room.NewsArticleDao
import com.example.articles.room.NewsArticleEntity
import com.example.articles.utils.ApiClient
import com.example.articles.utils.ApiInterface
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import retrofit2.Call
import retrofit2.Response

class ActivityMain : BaseActivity() {
    lateinit var bind: ActivityMainBinding
    lateinit var adapter: AdapterArticles
    var list: MutableList<Article> = mutableListOf()
    lateinit var apiInterface: ApiInterface
    val TAG = "ActivityMain"
    lateinit var dataBase: AtriclesDatabase
    lateinit var articleDao: NewsArticleDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)

        dataBase = AtriclesDatabase.getInstance(this)
        articleDao = dataBase.articlesDao()

        apiInterface = ApiClient.getClient().create(ApiInterface::class.java)
        adapter = AdapterArticles(this, list as ArrayList<Article>) {
            val detailJson = Gson().toJson(it)
            val intent = Intent(this@ActivityMain, DetailActivity::class.java).apply {
                putExtra("detailJson", detailJson)
            }
            startActivity(intent)
        }
        val linearLayoutManager = LinearLayoutManager(this)
        bind.rvArticles.layoutManager = linearLayoutManager
        bind.rvArticles.adapter = adapter

        getArticles()
    }

    fun getArticles() {
        bind.progressBar.visibility = View.VISIBLE
        if (isNetworkAvailable(this)) {
            val map = HashMap<String, String>()
            map.put("country", "us")
            map.put("category", "business")
            map.put("apiKey", "e97e871844b14f918148b540c625975c")

            val call: Call<ArticleModel> = apiInterface.getUSTopHeadlines(map)
            call.enqueue(object : retrofit2.Callback<ArticleModel> {
                override fun onResponse(
                    call: Call<ArticleModel?>,
                    response: Response<ArticleModel?>
                ) {
                    Log.d(TAG, "onResponse: " + Gson().toJson(response.body()))
                    bind.progressBar.visibility = View.GONE

                    if (response.body()?.status.equals("ok")) {
                        response.body().let {
                            val articles = response.body()?.articles
                            list.addAll(it!!.articles)
                            adapter.notifyDataSetChanged()

                            //save the data to DB
                            GlobalScope.launch(Dispatchers.IO) {
                                articleDao.deleteAll()
                                val localArticles = articles?.take(10)?.map {
                                    NewsArticleEntity(
                                        id = 0,
                                        articleName = it.source.name.toString(),
                                        authorName = it.author.toString(),
                                        articleDescription = it.description.toString(),
                                        articleDuration = it.publishedAt.toString(),
                                        articleImage = it.url.toString()
                                    )
                                } ?: emptyList()
                                articleDao.insertArticles(localArticles)
                            }
                        }


                    }

                }

                override fun onFailure(
                    call: Call<ArticleModel?>,
                    t: Throwable
                ) {
                    Log.d(TAG, "onFailure: running" + t.message)
                    bind.progressBar.visibility = View.GONE
                }

            })
        } else {
            GlobalScope.launch(Dispatchers.IO) {
                val cachedData = articleDao.getAllArticles()
                withContext(Dispatchers.Main) {
                    list.clear()
                    list.addAll(cachedData.map {
                        Article(
                            source = Source(it.id.toString(), it.articleName),
                            author = it.authorName,
                            title = it.articleName,
                            description = it.articleDescription,
                            url = "",
                            urlToImage = it.articleImage,
                            publishedAt = "",
                            content = ""
                        )
                    })
                    adapter.notifyDataSetChanged()
                    bind.progressBar.visibility = View.GONE
                    Toast.makeText(this@ActivityMain, "Showing Offline Data", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }


    }
}