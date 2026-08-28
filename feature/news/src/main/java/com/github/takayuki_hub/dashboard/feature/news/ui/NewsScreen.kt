package com.github.takayuki_hub.dashboard.feature.news.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.takayuki_hub.dashboard.core.model.Article // ドメインモデルの参照
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

// ① Stateful: ViewModelの依存と状態収集を担当
@Composable
fun NewsScreen(
    uiState: NewsUiState,
    onArticleClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    NewsScreenContent(
        uiState = uiState,
        onArticleClick = onArticleClick
    )
}

@Composable
private fun NewsScreenContent(
    uiState: NewsUiState,
    onArticleClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        when (uiState) {
            is NewsUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is NewsUiState.Success -> {
                if (uiState.articles.isEmpty()) {
                    Text(
                        text = "記事がありません",
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    NewsList(
                        articles = uiState.articles,
                        onArticleClick = onArticleClick
                    )
                }
            }

            is NewsUiState.Error -> {
                Text(
                    text = uiState.throwable.localizedMessage ?: "エラーが発生しました",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

// ③ リスト表示部分
@Composable
private fun NewsList(
    articles: List<Article>,
    onArticleClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(
            items = articles,
            key = { article -> article.url } // 一意のIDを渡すと描画パフォーマンス向上
        ) { article ->
            NewsItem(
                article = article,
                onClick = {
                    val encodedUrl = URLEncoder.encode(article.url, StandardCharsets.UTF_8.toString())
                    onArticleClick(encodedUrl)
                }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

// ④ リストの1行（記事アイテム）
@Composable
private fun NewsItem(
    article: Article,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ① 画像が存在する場合のみ Coil で描画
        if (!article.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = article.imageUrl,
                contentDescription = article.title,
                modifier = Modifier
                    .size(88.dp) // 画像サイズ
                    .clip(RoundedCornerShape(8.dp)), // 角丸処理
                contentScale = ContentScale.Crop // 枠に合わせてトリミング
            )
            Spacer(modifier = Modifier.width(12.dp))
        }

        // ② テキスト部分（タイトル＋説明文）
        Column(
            modifier = Modifier.weight(1f) // 残りの横幅を埋める
        ) {
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (!article.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = article.description!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}