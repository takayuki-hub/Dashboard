package com.github.takayuki_hub.dashboard.feature.weather.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// ① Stateful: ナビゲーションや Hilt(ViewModel) の接続を担当
@Composable
fun WeatherRoute(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // パーミッション要求用のランチャー
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (isGranted) {
            // 許可されたら位置情報を使って天気取得
            viewModel.fetchWeatherForCurrentLocation()
        } else {
            // 拒否された場合はデフォルト位置で取得するなどのハンドリング
        }
    }

    LaunchedEffect(Unit) {
        // 画面表示時にパーミッションを要求
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    WeatherScreen(
        uiState = uiState,
        onRefresh = { viewModel.fetchWeatherForCurrentLocation() },
        modifier = modifier
    )
}