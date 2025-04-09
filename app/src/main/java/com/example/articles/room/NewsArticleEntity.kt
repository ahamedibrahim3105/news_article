package com.example.articles.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class NewsArticleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val articleName:String,
    val authorName:String,
    val articleDescription:String,
    val articleDuration:String,
    val articleImage:String
)