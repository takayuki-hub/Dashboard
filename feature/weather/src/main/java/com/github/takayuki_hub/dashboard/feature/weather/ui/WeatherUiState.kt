package com.github.takayuki_hub.dashboard.feature.weather.ui

import com.github.takayuki_hub.dashboard.core.model.WeatherData

sealed interface WeatherUiState {
    /** 読み込み中 */
    data object Loading : WeatherUiState

    /** 取得成功 */
    data class Success(val weatherData: WeatherData) : WeatherUiState

    /** エラー発生 */
    data class Error(val message: String) : WeatherUiState
}