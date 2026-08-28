package com.github.takayuki_hub.dashboard.feature.home.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.takayuki_hub.dashboard.feature.home.ui.HomeUiState

@Composable
fun NewsCard(
    uiState: HomeUiState,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DashboardCard(
        title = "最新ニュース",
        onMoreClick = onMoreClick,
        modifier = modifier
    ) {
        when {
            // 全体ローディング中
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            }
            // ニュース一覧が存在する場合
            uiState.newsList.isNotEmpty() -> {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    uiState.newsList.forEach { news ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "・",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = news.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    lineHeight = 20.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
            // ニュースが空の場合
            else -> {
                Text(
                    text = "最新ニュースはありません",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}