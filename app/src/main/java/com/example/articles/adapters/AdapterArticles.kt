package com.example.articles.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.articles.databinding.ItemArticlesBinding
import com.example.articles.model.Article
import com.example.articles.model.Source
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class AdapterArticles(
    val context: Context,
    val list: ArrayList<Article>,
    val itemClicked: (Article) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "AdapterArticles"
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        return VHArticles(
            ItemArticlesBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        if (holder is VHArticles) {
            val position = holder.adapterPosition
            holder.setData(list[position], list[position].source)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class VHArticles(val bind: ItemArticlesBinding) : RecyclerView.ViewHolder(bind.root) {

        fun setData(dataArticle: Article, dataSource: Source) {
            Log.d(TAG, "setData: running")
            bind.txtArticleName.text = dataSource.name
            bind.txtAuthorName.text = dataArticle.author
            bind.txtArticleTitle.text = dataArticle.title
            bind.txtDuration.text = getTimeAgo(dataArticle.publishedAt)

            Glide
                .with(context)
                .load(dataArticle.urlToImage)
                .centerCrop()
                .into(bind.imgArticle)

            bind.layParent.setOnClickListener {
                itemClicked(dataArticle)
            }
        }

        fun getTimeAgo(timestamp: String): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")

            val pastTime = dateFormat.parse(timestamp)?.time ?: return "Unknown"
            val now = System.currentTimeMillis()
            val diff = now - pastTime

            return when {
                TimeUnit.MILLISECONDS.toMinutes(diff) < 1 -> "Just now"
                TimeUnit.MILLISECONDS.toMinutes(diff) < 60 -> "${
                    TimeUnit.MILLISECONDS.toMinutes(
                        diff
                    )
                } minutes ago"

                TimeUnit.MILLISECONDS.toHours(diff) < 24 -> "${TimeUnit.MILLISECONDS.toHours(diff)} hours ago"
                TimeUnit.MILLISECONDS.toDays(diff) < 2 -> "${TimeUnit.MILLISECONDS.toDays(diff)} day ago"
                TimeUnit.MILLISECONDS.toDays(diff) < 7 -> "${TimeUnit.MILLISECONDS.toDays(diff)} days ago"
                TimeUnit.MILLISECONDS.toDays(diff) < 30 -> "${TimeUnit.MILLISECONDS.toDays(diff) / 7} weeks ago"
                TimeUnit.MILLISECONDS.toDays(diff) < 365 -> "${TimeUnit.MILLISECONDS.toDays(diff) / 30} months ago"
                else -> "${TimeUnit.MILLISECONDS.toDays(diff) / 365} years ago"
            }
        }


    }
}