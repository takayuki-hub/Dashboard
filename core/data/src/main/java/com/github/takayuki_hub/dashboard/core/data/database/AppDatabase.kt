package com.github.takayuki_hub.dashboard.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.takayuki_hub.dashboard.core.data.database.dao.TaskDao
import com.github.takayuki_hub.dashboard.core.data.database.entity.TaskEntity

@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}