package com.example.articles.room

import android.content.Context
import androidx.room.Database
import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper

@Database(entities = [NewsArticleEntity::class], version = 1)
abstract class AtriclesDatabase: RoomDatabase() {

    abstract fun articlesDao(): NewsArticleDao
    companion object {
        private var INSTANCE:AtriclesDatabase?= null

        fun getInstance(context: Context):AtriclesDatabase{
            if(INSTANCE == null){
                INSTANCE = Room.databaseBuilder(context.applicationContext,
                    AtriclesDatabase::class.java,"news_articles_db").build()
            }
            return INSTANCE!!
        }
    }


}