package com.github.takayuki_hub.dashboard.feature.home.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.takayuki_hub.dashboard.core.model.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCard(
    tasks: List<Task>,
    onTaskCheckedChanged: (Task, Boolean) -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DashboardCard(
        title = "未完了タスク",
        onMoreClick = onMoreClick,
        modifier = modifier
    ) {
        if (tasks.isEmpty()) {
            Text(
                text = "未完了のタスクはありません 🎉",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            // ★ 高さを最大160dp（おおよそ3〜4件分）に制限し、はみ出た場合は内部スクロール
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .heightIn(max = 160.dp)
                    // ▼ ここからグラデーション（フェードアウト）処理を追加
                    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(
                                0.7f to Color.Black,       // 上部70%はそのまま表示
                                1.0f to Color.Transparent // 下端に向かって徐々に消える
                            ),
                            blendMode = BlendMode.DstIn
                        )
                    }
                    // ▲ ここまで
                    .verticalScroll(rememberScrollState())
            ) {
                tasks.forEach { task ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 6.dp) {
                            Checkbox(
                                checked = task.isCompleted,
                                onCheckedChange = { isChecked ->
                                    onTaskCheckedChanged(task, isChecked)
                                }
                            )
                        }

                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}