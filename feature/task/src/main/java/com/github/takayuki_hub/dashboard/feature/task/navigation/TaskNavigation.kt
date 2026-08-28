package com.github.takayuki_hub.dashboard.feature.task.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.takayuki_hub.dashboard.feature.task.ui.TaskRoute
import kotlinx.serialization.Serializable

@Serializable
object TaskDestination // 遷移キー

fun NavGraphBuilder.taskScreen() {
    composable<TaskDestination> {
        TaskRoute()
    }
}