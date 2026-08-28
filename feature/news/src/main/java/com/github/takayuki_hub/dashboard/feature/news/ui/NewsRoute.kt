package com.github.takayuki_hub.dashboard.feature.news.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// Stateful (ViewModel 接続)
@Composable
fun NewsRoute(
    onArticleClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    NewsScreen(
        uiState = uiState,
        onArticleClick = onArticleClick,
        modifier = modifier
    )
}