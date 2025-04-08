package com.example.articles.utils

import com.example.articles.model.ArticleModel
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.QueryMap


interface ApiInterface {


    @GET("v2/everything")
    fun getArticleData(@QueryMap map: HashMap<String,String>): Call<ArticleModel>

}