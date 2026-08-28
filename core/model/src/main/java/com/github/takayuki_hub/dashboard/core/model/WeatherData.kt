package com.github.takayuki_hub.dashboard.core.model

data class WeatherData(
    val temperatureCelsius: Double,
    val windSpeedKmH: Double,
    val weatherType: WeatherType,
    val locationName: String,
    val weeklyForecast: List<DailyForecast> = emptyList()
)

data class DailyForecast(
    val dateLabel: String,       // 例: "月", "火", "今日" など
    val weatherType: WeatherType,
    val maxTemp: Double,
    val minTemp: Double
)