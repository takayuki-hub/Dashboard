package com.github.takayuki_hub.dashboard.core.data.repository

import com.github.takayuki_hub.dashboard.core.data.mapper.toWeatherData
import com.github.takayuki_hub.dashboard.core.network.api.OpenMeteoApi
import com.github.takayuki_hub.dashboard.core.model.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: OpenMeteoApi
) : WeatherRepository {

    override suspend fun getWeatherData(
        latitude: Double,
        longitude: Double,
    ): Result<WeatherData> {
        return try {
            val response = api.getWeather(latitude, longitude)
            Result.success(response.toWeatherData())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ★ 新しい IF の実装
    override fun getWeatherDataStream(
        latitude: Double,
        longitude: Double
    ): Flow<Result<WeatherData>> = flow {
        val result = getWeatherData(latitude, longitude) // 既存の suspend 関数を再利用
        emit(result)
    }.flowOn(Dispatchers.IO)
}