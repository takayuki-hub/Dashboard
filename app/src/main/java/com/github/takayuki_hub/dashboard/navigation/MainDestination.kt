package com.github.takayuki_hub.dashboard.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.takayuki_hub.dashboard.feature.home.navigation.HomeDestination
import com.github.takayuki_hub.dashboard.feature.news.navigation.NewsDestination
import com.github.takayuki_hub.dashboard.feature.task.navigation.TaskDestination
import com.github.takayuki_hub.dashboard.feature.weather.navigation.WeatherDestination
import kotlin.reflect.KClass

enum class MainDestination(
    val route: Any,
    val routeClass: KClass<*>,
    val title: String,
    val icon: ImageVector
) {
    HOME(
        route = HomeDestination,
        routeClass = HomeDestination::class,
        title = "ホーム",
        icon = Icons.Default.Home
    ),
    WEATHER(
        route = WeatherDestination,
        routeClass = WeatherDestination::class,
        title = "天気",
        icon = Icons.Default.WbSunny
    ),
    NEWS(
        route = NewsDestination,
        routeClass = NewsDestination::class,
        title = "ニュース",
        icon = Icons.AutoMirrored.Filled.List
    ),
    TASK(
        route = TaskDestination,
        routeClass = TaskDestination::class,
        title = "タスク",
        icon = Icons.Default.Checklist
    )
}