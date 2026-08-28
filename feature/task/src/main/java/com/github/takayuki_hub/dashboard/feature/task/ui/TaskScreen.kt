package com.github.takayuki_hub.dashboard.feature.task.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.github.takayuki_hub.dashboard.core.model.Task

@Composable
fun TaskScreen(
    uiState: TaskUiState,
    onInputTitleChanged: (String) -> Unit,
    onAddTask: () -> Unit,
    onTaskCheckedChanged: (Task, Boolean) -> Unit,
    onDeleteTask: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "タスク管理",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- タスク入力エリア ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = uiState.inputTitle,
                onValueChange = onInputTitleChanged,
                placeholder = { Text("新しいタスクを追加...") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onAddTask) {
                Icon(Icons.Default.Add, contentDescription = "追加")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- タスク一覧リスト ---
        if (uiState.taskList.isEmpty()) {
            Text(
                text = "タスクはありません",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.taskList, key = { it.id }) { task ->
                    TaskItemRow(
                        task = task,
                        onCheckedChange = { isChecked -> onTaskCheckedChanged(task, isChecked) },
                        onDeleteClick = { onDeleteTask(task) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskItemRow(
    task: Task,
    onCheckedChange: (Boolean) -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = onCheckedChange
        )
        Text(
            text = task.title,
            style = MaterialTheme.typography.bodyLarge,
            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onDeleteClick) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "削除",
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}