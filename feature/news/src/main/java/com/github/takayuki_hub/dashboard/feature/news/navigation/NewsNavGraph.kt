package com.github.takayuki_hub.dashboard.feature.news.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.github.takayuki_hub.dashboard.feature.news.ui.NewsDetailScreen
import com.github.takayuki_hub.dashboard.feature.news.ui.NewsRoute
import kotlinx.serialization.Serializable

// 1. 住所（NavKey）
@Serializable
object NewsDestination

@Serializable
data class NewsDetailDestination(val url: String)

// 2. :app 向け公開拡張関数
fun NavGraphBuilder.newsScreen(
    onArticleClick: (String) -> Unit
) {
    composable<NewsDestination> {
        NewsRoute(onArticleClick = onArticleClick)
    }
}

// 2. NavHost 登録用拡張関数の定義
fun NavGraphBuilder.newsDetailScreen(
    onBackClick: () -> Unit
) {
    composable<NewsDetailDestination> { backStackEntry ->
        // URLパラメータの自動取得（手動デコード不要）
        val destination: NewsDetailDestination = backStackEntry.toRoute()

        NewsDetailScreen(
            url = destination.url,
            onBackClick = onBackClick
        )
    }
}