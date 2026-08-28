package com.github.takayuki_hub.dashboard.core.designsystem.icon // または適切なパッケージ

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.takayuki_hub.dashboard.core.model.WeatherType

/**
 * WeatherType に対応するマテリアルアイコンを取得する Mapper
 */
fun WeatherType.toIcon(): ImageVector {
    return when (this) {
        WeatherType.CLEAR_SKY -> Icons.Default.WbSunny
        WeatherType.CLOUDY -> Icons.Default.WbCloudy
        WeatherType.FOG -> Icons.Default.Cloud
        WeatherType.DRIZZLE -> Icons.Default.WaterDrop
        WeatherType.RAIN -> Icons.Default.Grain
        WeatherType.SNOW -> Icons.Default.AcUnit
        WeatherType.THUNDERSTORM -> Icons.Default.Thunderstorm
    }
}