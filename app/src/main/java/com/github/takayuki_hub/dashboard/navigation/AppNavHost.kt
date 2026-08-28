package com.github.takayuki_hub.dashboard.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.takayuki_hub.dashboard.feature.home.navigation.HomeDestination
import com.github.takayuki_hub.dashboard.feature.home.navigation.homeScreen
import com.github.takayuki_hub.dashboard.feature.news.navigation.NewsDestination
import com.github.takayuki_hub.dashboard.feature.news.navigation.NewsDetailDestination
import com.github.takayuki_hub.dashboard.feature.news.navigation.newsDetailScreen
import com.github.takayuki_hub.dashboard.feature.news.ui.NewsRoute
import com.github.takayuki_hub.dashboard.feature.news.navigation.newsScreen
import com.github.takayuki_hub.dashboard.feature.task.navigation.TaskDestination
import com.github.takayuki_hub.dashboard.feature.task.navigation.taskScreen
import com.github.takayuki_hub.dashboard.feature.weather.navigation.WeatherDestination
import com.github.takayuki_hub.dashboard.feature.weather.navigation.weatherScreen
import com.github.takayuki_hub.dashboard.feature.weather.ui.WeatherRoute

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination,
        modifier = modifier
    ) {
        // ① ホーム（ダッシュボード）

        homeScreen(
            onNavigateToWeather = {
                navController.navigateToTopLevelDestination(WeatherDestination)
            },
            onNavigateToNews = {
                navController.navigateToTopLevelDestination(NewsDestination)
            },
            onNavigateToTask = {
                navController.navigateToTopLevelDestination(TaskDestination)
            }
        )

        // ② 天気詳細画面
        weatherScreen()

        // ③ ニュース画面
        newsScreen(
            onArticleClick = { articleUrl ->
                navController.navigate(NewsDetailDestination(url = articleUrl))
            }
        )

        // ④ ニュース詳細画面
        newsDetailScreen(
            onBackClick = {
                navController.popBackStack()
            }
        )

        // ⑤ ニュース詳細画面
        taskScreen()
    }
}