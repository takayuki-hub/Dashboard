package com.github.takayuki_hub.dashboard.feature.home.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue

@Composable
fun HomeRoute(
    onNavigateToWeather: () -> Unit,
    onNavigateToNews: () -> Unit,
    onNavigateToTask: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel() // Hilt で ViewModel を注入
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onNavigateToWeather = onNavigateToWeather,
        onNavigateToNews = onNavigateToNews,
        onNavigateToTask = onNavigateToTask, // ★ HomeScreen へ伝播
        onTaskCheckedChanged = { task, isChecked -> // ★ ViewModel の処理へ繋ぐ
            viewModel.onTaskCheckedChanged(task, isChecked)
        },
        modifier = modifier
    )
}