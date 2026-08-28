package com.github.takayuki_hub.dashboard.core.data.location

import android.location.Location

interface LocationTracker {
    suspend fun getCurrentLocation(): Location?
    suspend fun getCityName(latitude: Double, longitude: Double): String?
}