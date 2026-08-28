package com.github.takayuki_hub.dashboard.core.network.dto

import com.google.gson.annotations.SerializedName

data class NetworkNewsResponse(
    @SerializedName("status") val status: String,
    @SerializedName("totalResults") val totalResults: Int,
    @SerializedName("articles") val articles: List<NetworkArticle>?
)
data class NetworkArticle(
    @SerializedName("source") val source: NetworkSource?,
    @SerializedName("author") val author: String?,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("url") val url: String,
    @SerializedName("urlToImage") val urlToImage: String?,
    @SerializedName("publishedAt") val publishedAt: String,
    @SerializedName("content") val content: String?
)

data class NetworkSource(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String
)