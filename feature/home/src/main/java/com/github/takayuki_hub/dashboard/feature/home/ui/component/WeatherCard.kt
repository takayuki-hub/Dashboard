package com.github.takayuki_hub.dashboard.feature.home.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.takayuki_hub.dashboard.core.designsystem.icon.toIcon
import com.github.takayuki_hub.dashboard.feature.home.ui.HomeUiState

@Composable
fun WeatherCard(
    uiState: HomeUiState,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DashboardCard(
        title = "今日の天気",
        onMoreClick = onMoreClick,
        modifier = modifier
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            }
            uiState.isWeatherError -> {
                Text(
                    text = "天気の取得に失敗しました",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            uiState.weather != null -> {
                val weather = uiState.weather
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = weather.weatherType.toIcon(),
                        contentDescription = weather.weatherType.description,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp) // 48.dp にするとさらに存在感アップ！
                    )

                    Column {
                        Text(
                            text = "${weather.temperatureCelsius.toInt()}°C",
                            style = MaterialTheme.typography.headlineMedium, // headlineSmall -> headlineMedium へ強弱を強化
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = weather.weatherType.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            else -> {
                Text(
                    text = "データなし",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}