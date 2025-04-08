package com.example.articles.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.articles.adapters.AdapterArticles
import com.example.articles.databinding.ActivityMainBinding
import com.example.articles.model.Article
import com.example.articles.model.ArticleModel
import com.example.articles.utils.ApiClient
import com.example.articles.utils.ApiInterface
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Response

class ActivityMain : AppCompatActivity() {
    lateinit var bind: ActivityMainBinding
    lateinit var adapter: AdapterArticles
    var list: MutableList<Article> = mutableListOf()
    lateinit var apiInterface: ApiInterface
    val TAG = "ActivityMain"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
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
        val map = HashMap<String, String>()
        map.put("q", "apple")
        map.put("from", "2025-03-31")
        map.put("to", "2025-03-31")
        map.put("sortBy", "popularity")
        map.put("apiKey", "e97e871844b14f918148b540c625975c")

        val call: Call<ArticleModel> = apiInterface.getArticleData(map)
        call.enqueue(object : retrofit2.Callback<ArticleModel> {
            override fun onResponse(
                call: Call<ArticleModel?>,
                response: Response<ArticleModel?>
            ) {
                Log.d(TAG, "onResponse: " + Gson().toJson(response.body()))
                bind.progressBar.visibility = View.GONE
                if (response.body()?.status.equals("ok")) {
                    response.body().let {
                        list.addAll(it!!.articles)
                        adapter.notifyDataSetChanged()
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

    }
}