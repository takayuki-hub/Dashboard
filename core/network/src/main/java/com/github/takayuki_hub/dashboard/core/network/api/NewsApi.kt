package com.github.takayuki_hub.dashboard.core.network.api

import com.github.takayuki_hub.dashboard.core.network.dto.NetworkNewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "jp",
        @Query("category") category: String = "sports",
        @Query("apiKey") apiKey: String
    ): NetworkNewsResponse

    @GET("v2/everything")
    suspend fun getNews(
        @Query("q") query: String = "Android", // キーワードを指定（例: Apple, Android, Techなど）
        @Query("sortBy") sortBy: String = "publishedAt",
        @Query("apiKey") apiKey: String
    ): NetworkNewsResponse
}