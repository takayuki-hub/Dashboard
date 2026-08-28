package com.github.takayuki_hub.dashboard.core.data.repository

import com.github.takayuki_hub.dashboard.core.model.WeatherData
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getWeatherData(
        latitude: Double,
        longitude: Double,
    ): Result<WeatherData>

    fun getWeatherDataStream(
        latitude: Double,
        longitude: Double
    ): Flow<Result<WeatherData>>
}