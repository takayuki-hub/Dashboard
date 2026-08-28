package com.github.takayuki_hub.dashboard.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.takayuki_hub.dashboard.feature.home.ui.component.NewsCard
import com.github.takayuki_hub.dashboard.feature.home.ui.component.TaskCard
import com.github.takayuki_hub.dashboard.feature.home.ui.component.WeatherCard
import com.github.takayuki_hub.dashboard.core.model.Task

@Composable
fun HomeScreen(
    uiState: HomeUiState, // UIの状態（天気やニュースのデータ）を受け取る
    onNavigateToWeather: () -> Unit,
    onNavigateToNews: () -> Unit,
    onNavigateToTask: () -> Unit, // ★ タスク詳細への遷移
    onTaskCheckedChanged: (Task, Boolean) -> Unit, // ★ タスクの完了チェック操作
    modifier: Modifier = Modifier
) {

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.08f)
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "マイダッシュボード",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // 1. 天気サマリーカード
        WeatherCard(
            uiState = uiState,
            onMoreClick = onNavigateToWeather
        )

        // 2. ニュースサマリーカード
        NewsCard(
            uiState = uiState,
            onMoreClick = onNavigateToNews
        )

        // 3. 未完了タスクカード（★追加！）
        TaskCard(
            tasks = uiState.taskList,
            onTaskCheckedChanged = onTaskCheckedChanged,
            onMoreClick = onNavigateToTask
        )
    }
}

