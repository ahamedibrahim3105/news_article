package com.example.articles.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NewsArticleDao {

    //suspend uses
    //function can be paused and resumed later, without blocking the thread it's running on.


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles:List<NewsArticleEntity>)

    @Query("SELECT * FROM articles")
    suspend fun getAllArticles():List<NewsArticleEntity>

    @Query("DELETE FROM articles")
    suspend fun deleteAll()
}