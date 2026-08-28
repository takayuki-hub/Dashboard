package com.github.takayuki_hub.dashboard.core.data.repository

import com.github.takayuki_hub.dashboard.core.model.Article
import com.github.takayuki_hub.dashboard.core.model.NewsSummary
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    suspend fun getTopSportsHeadlines(): Result<List<Article>>
    fun getLatestNewsSummary(limit: Int = 3): Flow<List<NewsSummary>>
}