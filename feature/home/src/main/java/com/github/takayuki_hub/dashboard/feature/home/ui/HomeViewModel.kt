package com.github.takayuki_hub.dashboard.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.takayuki_hub.dashboard.core.analytics.constants.AnalyticsEvents
import com.github.takayuki_hub.dashboard.core.analytics.repository.AnalyticsRepository
import com.github.takayuki_hub.dashboard.core.data.repository.NewsRepository
import com.github.takayuki_hub.dashboard.core.data.repository.TaskRepository
import com.github.takayuki_hub.dashboard.core.data.repository.WeatherRepository
import com.github.takayuki_hub.dashboard.core.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    weatherRepository: WeatherRepository,
    newsRepository: NewsRepository,
    private val taskRepository: TaskRepository,
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {

    init {
        // 画面表示イベントを記録
        viewModelScope.launch {
            analyticsRepository.logScreenView(AnalyticsEvents.SCREEN_HOME)
        }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        weatherRepository.getWeatherDataStream(latitude = 35.4437, longitude = 139.6380), // 緯度経度を指定
        newsRepository.getLatestNewsSummary(limit = 3),
        taskRepository.getAllTasks()
    ) { weatherResult, news, tasks ->
        HomeUiState(
            isLoading = false,
            weather = weatherResult.getOrNull(),
            newsList = news,
            isWeatherError = weatherResult.isFailure,
            taskList = tasks.filter { !it.isCompleted }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    // ★ タスクの完了状態切り替え
    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) {
        viewModelScope.launch {
            taskRepository.updateTask(task.copy(isCompleted = isChecked))
            // タスク編集イベントを記録
            analyticsRepository.logEvent(
                eventName = AnalyticsEvents.TASK_EDITED,
                params = emptyMap()
            )
        }
    }

    // ★ タスクの簡易追加
    fun onAddTask(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            taskRepository.insertTask(Task(title = title))
            // タスク作成イベントを記録
            analyticsRepository.logEvent(
                eventName = AnalyticsEvents.TASK_CREATE_CLICKED,
                params = emptyMap()
            )
        }
    }
}