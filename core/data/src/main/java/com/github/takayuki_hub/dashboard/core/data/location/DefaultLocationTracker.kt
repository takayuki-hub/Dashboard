package com.github.takayuki_hub.dashboard.core.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

class DefaultLocationTracker @Inject constructor(
    private val locationClient: FusedLocationProviderClient,
    @param:ApplicationContext private val context: Context
) : LocationTracker {

    override suspend fun getCurrentLocation(): Location? {
        // 権限チェック
        val hasAccessFineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasAccessCoarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        // GPS / ネットワーク位置情報が有効か確認
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if ((!hasAccessFineLocation && !hasAccessCoarseLocation) || !isGpsEnabled) {
            return null
        }

        return try {
            // 現在地を非同期で取得（高精度）
            locationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).await()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getCityName(latitude: Double, longitude: Double): String? = withContext(
        Dispatchers.IO
    ) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            // API Level 33 以降とそれ以前で互換性を保つ記述
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                var cityName: String? = null
                geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                    val address = addresses.firstOrNull()
                    cityName = address?.locality ?: address?.subAdminArea ?: address?.adminArea
                }
                // 同期的に結果を待つ場合は従来 API または Fetch の工夫が必要ですが、
                // 簡略化・安定化のため従来 API もフォールバックとして利用可能です
            }

            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            val address = addresses?.firstOrNull()
            address?.locality ?: address?.subAdminArea ?: address?.adminArea
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}