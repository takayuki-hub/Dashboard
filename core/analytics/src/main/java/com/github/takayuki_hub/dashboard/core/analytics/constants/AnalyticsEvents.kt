package com.github.takayuki_hub.dashboard.core.analytics.constants

/**
 * Firebase Analyticsイベント名の定数定義
 *
 * 全てのイベント名をスネークケース、最大40文字以内で定義します。
 *
 * 関連要件:
 * - NFR-201: システムはAnalyticsイベント名を定数で管理しなければならない
 * - EDGE-102: システムはイベント名を最大40文字に制限しなければならない
 */
object AnalyticsEvents {

    // ========================================================================
    // 画面遷移イベント（REQ-201）
    // ========================================================================

    /** ホーム画面表示（TC-201-01） */
    const val SCREEN_HOME = "home_screen"

    /** 天気画面表示（TC-201-02） */
    const val SCREEN_WEATHER = "weather_screen"

    /** ニュース画面表示（TC-201-03） */
    const val SCREEN_NEWS = "news_screen"

    /** タスク画面表示（TC-201-04） */
    const val SCREEN_TASK = "task_screen"

    // ========================================================================
    // ユーザー操作イベント（REQ-202, REQ-203）
    // ========================================================================

    /** タスク作成ボタンクリック（TC-202-01） */
    const val TASK_CREATE_CLICKED = "task_create_clicked"

    /** タスク編集（REQ-203） */
    const val TASK_EDITED = "task_edited"

    /** タスク削除（REQ-203） */
    const val TASK_DELETED = "task_deleted"

    /** ニュース記事クリック（TC-202-02） */
    const val NEWS_ARTICLE_CLICKED = "news_article_clicked"

    /** 天気データ更新（REQ-208） */
    const val WEATHER_REFRESHED = "weather_refreshed"

    // ========================================================================
    // エラー・例外イベント（REQ-204, REQ-205）
    // ========================================================================

    /** ネットワークエラー */
    const val NETWORK_ERROR = "network_error"

    /** 天気データ取得失敗 */
    const val WEATHER_FETCH_FAILED = "weather_fetch_failed"

    /** ニュース取得失敗 */
    const val NEWS_FETCH_FAILED = "news_fetch_failed"

    /** 例外発生 */
    const val EXCEPTION_OCCURRED = "exception_occurred"

    // ========================================================================
    // アプリライフサイクルイベント（REQ-206, REQ-207）
    // ========================================================================

    /** アプリ起動 */
    const val APP_LAUNCHED = "app_launched"

    /** セッション開始 */
    const val SESSION_STARTED = "session_started"

    /** セッション終了 */
    const val SESSION_ENDED = "session_ended"
}
