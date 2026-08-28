package com.github.takayuki_hub.dashboard.core.data.repository

import com.github.takayuki_hub.dashboard.core.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    // 全タスクをリアルタイム監視可能な Flow で取得
    fun getAllTasks(): Flow<List<Task>>

    // タスクの追加・更新・削除
    suspend fun insertTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
}