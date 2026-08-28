package com.github.takayuki_hub.dashboard.feature.weather.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.github.takayuki_hub.dashboard.core.model.DailyForecast
import com.github.takayuki_hub.dashboard.core.model.WeatherData
import com.github.takayuki_hub.dashboard.core.model.WeatherType
import com.github.takayuki_hub.dashboard.core.designsystem.icon.toIcon

@Composable
fun WeatherScreen(
    uiState: WeatherUiState,         // ① 状態（データ）を外から受け取る
    onRefresh: () -> Unit,          // ② イベント（操作）を外から受け取る
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            is WeatherUiState.Loading -> {
                CircularProgressIndicator()
            }

            is WeatherUiState.Success -> {
                WeatherContent(
                    weatherData = uiState.weatherData,
                    onRefresh = onRefresh
                )
            }

            is WeatherUiState.Error -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRefresh) {
                        Text("再試行")
                    }
                }
            }
        }
    }
}

/**
 * データ表示用のコンポーザブル（UI のみ担当）
 */
@Composable
private fun WeatherContent(
    weatherData: WeatherData,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Column で全体を囲み、縦方向に並べる
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // ① 地名表示（📍アイコン付き）
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "現在地",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = weatherData.locationName.ifBlank { "現在地" },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        // ② メインの天気カード
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 天気状態（晴れ、曇りなど）
                Text(
                    text = weatherData.weatherType.description,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // メインの気温表示
                Text(
                    text = "${weatherData.temperatureCelsius}°C",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // 詳細情報（風速など）
                Text(
                    text = "風速: ${weatherData.windSpeedKmH} km/h",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }

        if (weatherData.weeklyForecast.isNotEmpty()) {
            WeeklyForecastSection(forecastList = weatherData.weeklyForecast)
        }

        // ③ 更新ボタン（アイコン付き）
        Button(
            onClick = onRefresh,
            shape = RoundedCornerShape(50)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("更新")
        }
    }
}

@Composable
private fun WeeklyForecastSection(
    forecastList: List<DailyForecast>,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "週間予報",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            forecastList.forEach { forecast ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 曜日（固定幅）
                    Text(
                        text = forecast.dateLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(64.dp)
                    )

                    // 天気アイコン
                    Icon(
                        imageVector = forecast.weatherType.toIcon(),
                        contentDescription = forecast.weatherType.description,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )

                    // 最高 / 最低気温
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "${forecast.maxTemp.toInt()}°",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${forecast.minTemp.toInt()}°",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

// ③ プレビュー例（ViewModel なしで各状態のレイアウトを確認できる）
@Preview(showBackground = true)
@Composable
private fun WeatherScreenSuccessPreview() {
    MaterialTheme {
        WeatherScreen(
            uiState = WeatherUiState.Success(
                weatherData = WeatherData(
                    temperatureCelsius = 22.5,
                    windSpeedKmH = 12.0,
                    weatherType = WeatherType.CLEAR_SKY,
                    locationName = "東京",
                    weeklyForecast = listOf(
                        DailyForecast("8/22(土)", WeatherType.CLEAR_SKY, 29.0, 22.0),
                        DailyForecast("8/23(日)", WeatherType.CLEAR_SKY, 30.0, 23.0),
                        DailyForecast("8/24(月)", WeatherType.THUNDERSTORM, 27.0, 21.0),
                        DailyForecast("8/25(火)", WeatherType.SNOW, 25.0, 20.0),
                        DailyForecast("8/26(水)", WeatherType.CLEAR_SKY, 28.0, 22.0),
                        DailyForecast("8/27(木)", WeatherType.CLEAR_SKY, 28.0, 22.0),
                        DailyForecast("8/28(金)", WeatherType.CLEAR_SKY, 28.0, 22.0)
                    )
                )
            ),
            onRefresh = {}
        )
    }
}