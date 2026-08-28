package com.github.takayuki_hub.dashboard.feature.weather.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.takayuki_hub.dashboard.feature.weather.ui.WeatherRoute
import kotlinx.serialization.Serializable

// 1. 住所（NavKey）
@Serializable
object WeatherDestination

// 2. :app 向け公開拡張関数
fun NavGraphBuilder.weatherScreen(
) {
    composable<WeatherDestination> {
        WeatherRoute()
    }
}