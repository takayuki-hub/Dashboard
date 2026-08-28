package com.github.takayuki_hub.dashboard.core.data.mapper

import com.github.takayuki_hub.dashboard.core.model.WeatherData
import com.github.takayuki_hub.dashboard.core.model.WeatherType
import com.github.takayuki_hub.dashboard.core.network.dto.NetworkWeatherResponse

fun NetworkWeatherResponse.toWeatherData(locationName: String = "現在地"): WeatherData {
    val current = this.currentWeather
        ?: throw IllegalStateException("Weather data is missing from API response")

    return WeatherData(
        temperatureCelsius = current.temperature,
        windSpeedKmH = current.windspeed,
        weatherType = WeatherType.fromWmoCode(current.weathercode),
        locationName = locationName, // 追加
    )
}