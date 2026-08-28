package com.github.takayuki_hub.dashboard.core.model

/**
 * ホーム（ダッシュボード）などで利用するニュースのサマリー情報
 */
data class NewsSummary(
    val id: String,          // 記事詳細へのナビゲーション等に使うID
    val title: String,       // ニュースのタイトル
    val publishedAt: String? = null // 必要に応じて公開日時など（任意）
)