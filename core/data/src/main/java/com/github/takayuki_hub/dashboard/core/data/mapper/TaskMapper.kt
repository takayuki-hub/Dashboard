package com.github.takayuki_hub.dashboard.core.data.mapper

import com.github.takayuki_hub.dashboard.core.data.database.entity.TaskEntity
import com.github.takayuki_hub.dashboard.core.model.Task

// DB Entity -> Domain Model
fun TaskEntity.toExternalModel(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        isCompleted = isCompleted,
        createdAt = createdAt
    )
}

// Domain Model -> DB Entity
fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        isCompleted = isCompleted,
        createdAt = createdAt
    )
}