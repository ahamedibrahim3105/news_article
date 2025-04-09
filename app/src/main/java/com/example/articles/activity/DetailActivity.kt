package com.example.articles.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.articles.databinding.ActivityDetailBinding
import com.example.articles.model.Article
import com.google.gson.Gson

class DetailActivity : BaseActivity() {

    private lateinit var bind: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(bind.root)

        val articleJson = intent.getStringExtra("detailJson")
        val articleData = Gson().fromJson(articleJson, Article::class.java)

        bind.txtTitle.text = articleData.source.name
        bind.txtArticleTitle.text = articleData.title
        bind.txtDescription.text = articleData.description
        bind.txtContent.text = articleData.content

        Glide
            .with(this@DetailActivity)
            .load(articleData.urlToImage)
            .centerCrop()
            .into(bind.imgArticle);

        bind.imgBackArrow.setOnClickListener { finish() }

    }
}