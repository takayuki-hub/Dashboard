package com.github.takayuki_hub.dashboard.feature.task.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.takayuki_hub.dashboard.core.data.repository.TaskRepository // ※お使いのリポジトリパスに合わせて調整してください
import com.github.takayuki_hub.dashboard.core.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _inputTitle = MutableStateFlow("")

    val uiState: StateFlow<TaskUiState> = combine(
        taskRepository.getAllTasks(),
        _inputTitle
    ) { tasks, input ->
        TaskUiState(
            taskList = tasks,
            isLoading = false,
            inputTitle = input
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskUiState(isLoading = true)
    )

    // 入力テキストの変更
    fun onInputTitleChanged(newTitle: String) {
        _inputTitle.value = newTitle
    }

    // タスク追加
    fun onAddTask() {
        val title = _inputTitle.value
        if (title.isBlank()) return

        viewModelScope.launch {
            taskRepository.insertTask(Task(title = title, isCompleted = false))
            _inputTitle.value = "" // 入力欄をクリア
        }
    }

    // 完了チェック切り替え
    fun onTaskCheckedChanged(task: Task, isCompleted: Boolean) {
        viewModelScope.launch {
            taskRepository.updateTask(task.copy(isCompleted = isCompleted))
        }
    }

    // タスク削除
    fun onDeleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }
}