package com.github.takayuki_hub.dashboard.feature.task.ui

import com.github.takayuki_hub.dashboard.core.model.Task

data class TaskUiState(
    val taskList: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val inputTitle: String = "" // 新規入力中のタスク名
)