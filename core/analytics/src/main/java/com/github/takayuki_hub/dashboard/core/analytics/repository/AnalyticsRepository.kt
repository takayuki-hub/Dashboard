package com.github.takayuki_hub.dashboard.core.analytics.repository

/**
 * Firebase Analyticsのイベント記録機能を提供するRepositoryインターフェース
 *
 * 全メソッドはsuspend関数として定義され、Coroutinesによる非同期実行をサポートします。
 * ViewModelから`viewModelScope.launch`で呼び出すことを想定しています。
 *
 * 関連要件:
 * - REQ-102: システムはAnalyticsRepositoryインターフェースを:core:analyticsモジュールに定義しなければならない
 * - REQ-104: システムはAnalyticsRepositoryをHiltでDI可能にしなければならない
 * - NFR-001: システムはAnalyticsイベント記録がUIスレッドをブロックしてはならない
 */
interface AnalyticsRepository {

    /**
     * カスタムイベントを記録します
     *
     * @param eventName イベント名（最大40文字、スネークケース推奨）
     * @param params イベントパラメータ（最大25個）
     *
     * 使用例:
     * ```kotlin
     * analyticsRepository.logEvent(
     *     eventName = AnalyticsEvents.TASK_CREATE_CLICKED,
     *     params = emptyMap()
     * )
     * ```
     *
     * 関連要件:
     * - REQ-201: システムは画面遷移イベントを記録しなければならない
     * - REQ-202: システムはボタンクリックなどのユーザー操作イベントを記録しなければならない
     * - REQ-204: システムはエラー発生イベントを記録しなければならない
     * - EDGE-101: システムは1イベントあたり最大25個のパラメータを記録できなければならない
     * - EDGE-102: システムはイベント名を最大40文字に制限しなければならない
     */
    suspend fun logEvent(
        eventName: String,
        params: Map<String, Any>
    )

    /**
     * ユーザープロパティを設定します
     *
     * ユーザープロパティはアプリライフサイクル全体で保持され、
     * Firebase Consoleでのユーザーセグメント分析に使用されます。
     *
     * @param name プロパティ名（最大40文字）
     * @param value プロパティ値（文字列）
     *
     * 使用例:
     * ```kotlin
     * analyticsRepository.setUserProperty(
     *     name = "app_version",
     *     value = BuildConfig.VERSION_NAME
     * )
     * ```
     *
     * 関連要件:
     * - REQ-211: システムはユーザープロパティを設定できなければならない
     * - REQ-212: システムはカスタムユーザー属性を記録できなければならない
     * - REQ-213: システムはユーザープロパティをアプリライフサイクル全体で保持しなければならない
     * - EDGE-103: システムはパラメータ名を最大40文字に制限しなければならない
     */
    suspend fun setUserProperty(
        name: String,
        value: String
    )

    /**
     * 画面表示イベント（screen_view）を記録します
     *
     * Firebase Analyticsの標準イベント`screen_view`を使用して、
     * 画面遷移を記録します。
     *
     * @param screenName 画面名（例: "home_screen", "weather_screen"）
     *
     * 使用例:
     * ```kotlin
     * analyticsRepository.logScreenView(
     *     screenName = AnalyticsEvents.SCREEN_HOME
     * )
     * ```
     *
     * 関連要件:
     * - REQ-201: システムは画面遷移イベントを記録しなければならない
     * - TC-201-01~TC-201-04: 各画面表示時のイベント記録テストケース
     */
    suspend fun logScreenView(
        screenName: String
    )
}
