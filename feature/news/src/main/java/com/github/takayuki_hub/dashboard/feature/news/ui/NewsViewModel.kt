package com.github.takayuki_hub.dashboard.feature.news.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.takayuki_hub.dashboard.core.data.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<NewsUiState>(NewsUiState.Loading)
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    init {
        fetchNews()
    }

    fun fetchNews() {
        viewModelScope.launch {
            _uiState.value = NewsUiState.Loading
            newsRepository.getTopSportsHeadlines()
                .onSuccess { articles ->
                    _uiState.value = NewsUiState.Success(articles)
                }
                .onFailure { throwable ->
                    _uiState.value = NewsUiState.Error(throwable)
                }
        }
    }
}