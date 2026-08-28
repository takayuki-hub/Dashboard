package com.github.takayuki_hub.dashboard.core.data.repository

import android.util.Log
import com.github.takayuki_hub.dashboard.core.model.Article
import com.github.takayuki_hub.dashboard.core.network.api.NewsApi
import com.github.takayuki_hub.dashboard.core.network.dto.NetworkArticle
import javax.inject.Inject
import com.github.takayuki_hub.dashboard.core.data.BuildConfig
import com.github.takayuki_hub.dashboard.core.model.NewsSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NewsRepositoryImpl @Inject constructor(
    private val newsApi: NewsApi
) : NewsRepository {

    override suspend fun getTopSportsHeadlines(): Result<List<Article>> {
        return runCatching {
            Log.d("NewsApi", "API Key: ${BuildConfig.NEWS_API_KEY}")
//            val response = newsApi.getTopHeadlines(
            val response = newsApi.getNews(
//                country = "jp",
//                category = "sports",
                apiKey = BuildConfig.NEWS_API_KEY // BuildConfig から取得
            )

            Log.d("NewsApi", "Response status: ${response.status}, total: ${response.totalResults}")

            // DTO -> Domain Model のマッピング
            // articles が null の場合は空リストを返すように安全にマッピング
            response.articles?.map { it.toExternalModel() } ?: emptyList()
        }.onFailure { e ->
            // 2. 例外が発生している場合はここでエラー内容を出力
            Log.e("NewsApi", "Fetch failed", e)
        }
    }

    // ★ 新しい IF の実装
    override fun getLatestNewsSummary(limit: Int): Flow<List<NewsSummary>> = flow {
        val result = runCatching {
            val response = newsApi.getNews(apiKey = BuildConfig.NEWS_API_KEY)
            response.articles
                ?.map { it.toSummaryModel() }
                ?.take(limit) ?: emptyList()
        }.getOrElse { e ->
            Log.e("NewsApi", "Fetch summary failed", e)
            emptyList()
        }
        emit(result)
    }
}

// 変換用 Mapper 関数
private fun NetworkArticle.toExternalModel(): Article {
    return Article(
        title = title ?: "(タイトルなし)",
        description = description,
        url = url,
        imageUrl = urlToImage,
        publishedAt = publishedAt?: "",
        sourceName = source?.name ?: "Unknown"
    )
}

private fun NetworkArticle.toSummaryModel(): NewsSummary {
    return NewsSummary(
        id = url, // ID としてユニークな URL
        title = title ?: "(タイトルなし)",
        publishedAt = publishedAt
    )
}