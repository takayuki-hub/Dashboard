package com.github.takayuki_hub.dashboard.core.data.repository

import com.github.takayuki_hub.dashboard.core.data.database.dao.TaskDao
import com.github.takayuki_hub.dashboard.core.data.mapper.toEntity
import com.github.takayuki_hub.dashboard.core.data.mapper.toExternalModel
import com.github.takayuki_hub.dashboard.core.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getAllTasks(): Flow<List<Task>> {
        // TaskEntity のリストを Flow の中で自動的に List<Task> へマッピング
        return taskDao.getAllTasks().map { entities ->
            entities.map { it.toExternalModel() }
        }
    }

    override suspend fun insertTask(task: Task) {
        taskDao.insertTask(task.toEntity())
    }

    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.toEntity())
    }
}