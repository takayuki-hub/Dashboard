package com.github.takayuki_hub.dashboard.feature.home.ui

import com.github.takayuki_hub.dashboard.core.model.NewsSummary
import com.github.takayuki_hub.dashboard.core.model.Task
import com.github.takayuki_hub.dashboard.core.model.WeatherData

data class HomeUiState(
    val isLoading: Boolean = true,
    val weather: WeatherData? = null,
    val newsList: List<NewsSummary> = emptyList(),
    val isWeatherError: Boolean = false,
    val taskList: List<Task> = emptyList(),
)