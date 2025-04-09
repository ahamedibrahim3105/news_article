package com.example.articles

import com.example.articles.utils.ApiInterface
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class NetworkTest {
    lateinit var mockWebServer: MockWebServer
    lateinit var api: ApiInterface

    @Before
    fun setUp(){
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(ApiInterface::class.java)

    }

    @After
    fun tearDown(){
        mockWebServer.shutdown()
    }

    @Test
    fun getaArticlesReturnSuccess(){
        val mockResponse = MockResponse()
        mockResponse.setBody("""
            {
              "status": "ok",
              "totalResults": 1,
              "articles": [{
                "source": { "id": null, "name": "MockSource" },
                "author": "John Doe",
                "title": "Test Article",
                "description": "Description here",
                "url": "http://example.com",
                "urlToImage": "http://image.com/img.jpg",
                "publishedAt": "2025-04-09T10:00:00Z",
                "content": "Full content"
              }]
            }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)
        val map = HashMap<String, String>()
        map["country"] = "us"
        map["category"] = "business"
        map["apiKey"] = "dummy"
        val response = api.getUSTopHeadlines(map).execute()

        Assert.assertTrue(response.isSuccessful)
        Assert.assertEquals("ok", response.body()?.status)
        Assert.assertEquals(1, response.body()?.articles?.size)

    }
}