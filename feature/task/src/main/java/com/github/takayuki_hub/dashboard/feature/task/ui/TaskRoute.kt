package com.github.takayuki_hub.dashboard.feature.task.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TaskRoute(
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TaskScreen(
        uiState = uiState,
        onInputTitleChanged = viewModel::onInputTitleChanged,
        onAddTask = viewModel::onAddTask,
        onTaskCheckedChanged = viewModel::onTaskCheckedChanged,
        onDeleteTask = viewModel::onDeleteTask,
        modifier = modifier
    )
}