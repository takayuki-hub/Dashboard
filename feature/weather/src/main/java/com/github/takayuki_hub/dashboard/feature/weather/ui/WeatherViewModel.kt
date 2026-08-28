package com.github.takayuki_hub.dashboard.feature.weather.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.takayuki_hub.dashboard.core.data.location.LocationTracker
import com.github.takayuki_hub.dashboard.core.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationTracker: LocationTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        fetchWeatherForCurrentLocation()
    }

    fun fetchWeatherForCurrentLocation() {
        viewModelScope.launch {
            val location = locationTracker.getCurrentLocation()

            // GPS取得失敗時はデフォルト座標（例: 横浜 35.4437, 139.6380）を使用
            val lat = location?.latitude ?: 35.4437
            val lng = location?.longitude ?: 139.6380

            // 並列で天気データと地名を取得
            val weatherDeferred = async { repository.getWeatherData(lat, lng) }
            val cityNameDeferred = async { locationTracker.getCityName(lat, lng) }

            val weatherResult = weatherDeferred.await()
            val cityName = cityNameDeferred.await() ?: "現在地"

            weatherResult
                .onSuccess { weatherData ->
                    _uiState.value = WeatherUiState.Success(
                        weatherData = weatherData.copy(locationName = cityName)
                    )
                }
                .onFailure { exception ->
                    _uiState.value = WeatherUiState.Error(
                        exception.localizedMessage ?: "天気の取得に失敗しました"
                    )
                }
        }
    }

    fun fetchWeather(latitude: Double = 35.4437, longitude: Double = 139.6380) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading

            repository.getWeatherData(latitude, longitude)
                .onSuccess { weatherData ->
                    _uiState.value = WeatherUiState.Success(weatherData)
                }
                .onFailure { exception ->
                    _uiState.value = WeatherUiState.Error(
                        exception.localizedMessage ?: "天気の取得に失敗しました"
                    )
                }
        }
    }
}