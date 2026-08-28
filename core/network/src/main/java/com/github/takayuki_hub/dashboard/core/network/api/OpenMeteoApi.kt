package com.github.takayuki_hub.dashboard.core.network.api

import com.github.takayuki_hub.dashboard.core.network.dto.NetworkWeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoApi {
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current_weather") currentWeather: Boolean = true,
        @Query("daily") daily: List<String> = listOf(
            "weathercode",
            "temperature_2m_max",
            "temperature_2m_min"
        ),
        @Query("timezone") timezone: String = "auto"
    ): NetworkWeatherResponse
}