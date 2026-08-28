package com.github.takayuki_hub.dashboard.core.model

enum class WeatherType(val description: String) {
    CLEAR_SKY("快晴"),
    CLOUDY("曇り"),
    FOG("霧"),
    DRIZZLE("霧雨"),
    RAIN("雨"),
    SNOW("雪"),
    THUNDERSTORM("雷雨");

    companion object {
        /**
         * Open-MeteoのWMOコード（数値）を受け取り、対応する WeatherType を返す
         */
        fun fromWmoCode(code: Int): WeatherType {
            return when (code) {
                0 -> CLEAR_SKY
                1, 2, 3 -> CLOUDY
                45, 48 -> FOG
                51, 53, 55, 56, 57 -> DRIZZLE
                61, 63, 65, 66, 67, 80, 81, 82 -> RAIN
                71, 73, 75, 77, 85, 86 -> SNOW
                95, 96, 99 -> THUNDERSTORM
                else -> CLEAR_SKY // 未知のコードが来た場合のデフォルト
            }
        }
    }
}