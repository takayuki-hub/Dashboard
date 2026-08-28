package com.github.takayuki_hub.dashboard.core.network.dto

import com.google.gson.annotations.SerializedName

data class NetworkWeatherResponse(
    @SerializedName("current_weather")
    val currentWeather: NetworkCurrentWeather?
)
data class NetworkCurrentWeather(
    val temperature: Double,
    val windspeed: Double,
    val weathercode: Int
)