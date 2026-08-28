package com.github.takayuki_hub.dashboard.feature.news.ui

import com.github.takayuki_hub.dashboard.core.model.Article

sealed interface NewsUiState {
    data object Loading : NewsUiState
    data class Success(val articles: List<Article>) : NewsUiState
    data class Error(val throwable: Throwable) : NewsUiState
}