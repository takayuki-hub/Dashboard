package com.github.takayuki_hub.dashboard.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.takayuki_hub.dashboard.feature.home.ui.HomeRoute
import kotlinx.serialization.Serializable

@Serializable
object HomeDestination // 遷移キー（Destination）

fun NavGraphBuilder.homeScreen(
    onNavigateToWeather: () -> Unit,
    onNavigateToNews: () -> Unit,
    onNavigateToTask: () -> Unit
) {
    composable<HomeDestination> {
        HomeRoute( // モジュール内部の Stateful Composable を呼ぶ
            onNavigateToWeather = onNavigateToWeather,
            onNavigateToNews = onNavigateToNews,
            onNavigateToTask = onNavigateToTask
        )
    }
}