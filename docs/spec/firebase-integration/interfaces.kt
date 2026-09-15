// Firebaseの導入 Kotlinインターフェース定義
// 作成日: 2026-09-12
// 関連要件定義: requirements.md
// 関連アーキテクチャ: architecture.md

// パッケージ: com.github.takayuki_hub.dashboard.core.analytics.repository

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

// ============================================================================
// パッケージ: com.github.takayuki_hub.dashboard.core.analytics.repository

/**
 * AnalyticsRepositoryの実装クラス
 *
 * FirebaseAnalyticsインスタンスをHiltでインジェクトし、
 * suspend関数とwithContext(Dispatchers.IO)による非ブロッキング実行を実現します。
 *
 * 関連要件:
 * - REQ-103: システムはAnalyticsRepositoryImpl実装クラスを:core:analyticsモジュールに定義しなければならない
 * - REQ-104: システムはAnalyticsRepositoryをHiltでDI可能にしなければならない
 * - NFR-001: システムはAnalyticsイベント記録がUIスレッドをブロックしてはならない
 */
class AnalyticsRepositoryImpl @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsRepository {

    /**
     * カスタムイベントを記録します
     *
     * 実装詳細:
     * - withContext(Dispatchers.IO)でバックグラウンドスレッド実行
     * - パラメータ数が25個を超える場合は警告ログを出力し、最初の25個のみ記録
     * - 個人情報（PII）を含むパラメータを除外
     */
    override suspend fun logEvent(
        eventName: String,
        params: Map<String, Any>
    ) {
        withContext(Dispatchers.IO) {
            // パラメータ数制限チェック（EDGE-101）
            if (params.size > 25) {
                Log.w(
                    TAG,
                    "Event $eventName has ${params.size} params (max 25). " +
                        "Only first 25 will be logged."
                )
            }

            // 個人情報（PII）除外（NFR-101）
            val sanitizedParams = params.filterNot { (key, _) ->
                key.contains("email", ignoreCase = true) ||
                    key.contains("phone", ignoreCase = true) ||
                    key.contains("name", ignoreCase = true) ||
                    key.contains("address", ignoreCase = true)
            }

            // 最大25個に制限
            val limitedParams = sanitizedParams.toList().take(25).toMap()

            // Firebase Analyticsに記録
            firebaseAnalytics.logEvent(
                eventName,
                bundleOf(*limitedParams.toList().toTypedArray())
            )
        }
    }

    /**
     * ユーザープロパティを設定します
     *
     * 実装詳細:
     * - withContext(Dispatchers.IO)でバックグラウンドスレッド実行
     * - プロパティ値は文字列として保存
     */
    override suspend fun setUserProperty(
        name: String,
        value: String
    ) {
        withContext(Dispatchers.IO) {
            firebaseAnalytics.setUserProperty(name, value)
        }
    }

    /**
     * 画面表示イベント（screen_view）を記録します
     *
     * 実装詳細:
     * - Firebase Analytics標準イベント`FirebaseAnalytics.Event.SCREEN_VIEW`を使用
     * - パラメータにscreen_nameとscreen_classを含める
     */
    override suspend fun logScreenView(
        screenName: String
    ) {
        withContext(Dispatchers.IO) {
            val params = mapOf(
                AnalyticsParams.SCREEN_NAME to screenName,
                AnalyticsParams.SCREEN_CLASS to screenName
            )
            firebaseAnalytics.logEvent(
                FirebaseAnalytics.Event.SCREEN_VIEW,
                bundleOf(*params.toList().toTypedArray())
            )
        }
    }

    companion object {
        private const val TAG = "AnalyticsRepository"
    }
}

// ============================================================================
// パッケージ: com.github.takayuki_hub.dashboard.core.analytics.constants

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

// ============================================================================
// パッケージ: com.github.takayuki_hub.dashboard.core.analytics.constants

/**
 * Firebase Analyticsパラメータ名の定数定義
 *
 * 全てのパラメータ名をスネークケース、最大40文字以内で定義します。
 *
 * 関連要件:
 * - NFR-201: システムはAnalyticsイベント名を定数で管理しなければならない
 * - EDGE-103: システムはパラメータ名を最大40文字に制限しなければならない
 */
object AnalyticsParams {

    // ========================================================================
    // Firebase標準パラメータ
    // ========================================================================

    /** 画面名（Firebase標準） */
    const val SCREEN_NAME = "screen_name"

    /** 画面クラス（Firebase標準） */
    const val SCREEN_CLASS = "screen_class"

    // ========================================================================
    // カスタムパラメータ - ユーザー操作
    // ========================================================================

    /** タスクID */
    const val TASK_ID = "task_id"

    /** 記事ID */
    const val ARTICLE_ID = "article_id"

    /** ナビゲーション先 */
    const val NAVIGATION_DESTINATION = "navigation_destination"

    // ========================================================================
    // カスタムパラメータ - エラー・例外
    // ========================================================================

    /** エラータイプ */
    const val ERROR_TYPE = "error_type"

    /** エラーメッセージ（100文字制限） */
    const val ERROR_MESSAGE = "error_message"

    // ========================================================================
    // カスタムパラメータ - アプリ情報
    // ========================================================================

    /** アプリバージョン */
    const val APP_VERSION = "app_version"
}

// ============================================================================
// パッケージ: com.github.takayuki_hub.dashboard.core.analytics.di

/**
 * Analytics関連のHilt DIモジュール
 *
 * FirebaseAnalyticsインスタンスとAnalyticsRepositoryの提供を行います。
 *
 * 関連要件:
 * - REQ-104: システムはAnalyticsRepositoryをHiltでDI可能にしなければならない
 * - REQ-001: システムはアプリケーション起動時にFirebase Analyticsを自動初期化しなければならない
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {

    /**
     * AnalyticsRepositoryの実装をバインド
     */
    @Binds
    @Singleton
    abstract fun bindAnalyticsRepository(
        impl: AnalyticsRepositoryImpl
    ): AnalyticsRepository

    companion object {
        /**
         * FirebaseAnalyticsインスタンスを提供
         *
         * @param context Applicationコンテキスト
         * @return FirebaseAnalyticsインスタンス
         */
        @Provides
        @Singleton
        fun provideFirebaseAnalytics(
            @ApplicationContext context: Context
        ): FirebaseAnalytics {
            return FirebaseAnalytics.getInstance(context)
        }
    }
}

// ============================================================================
// 必要なインポート
// ============================================================================

// import android.content.Context
// import android.os.Bundle
// import android.util.Log
// import androidx.core.os.bundleOf
// import com.google.firebase.analytics.FirebaseAnalytics
// import dagger.Binds
// import dagger.Module
// import dagger.Provides
// import dagger.hilt.InstallIn
// import dagger.hilt.android.qualifiers.ApplicationContext
// import dagger.hilt.components.SingletonComponent
// import kotlinx.coroutines.Dispatchers
// import kotlinx.coroutines.withContext
// import javax.inject.Inject
// import javax.inject.Singleton

// ============================================================================
// テスト用モック実装（NFR-202）
// ============================================================================

/**
 * AnalyticsRepositoryのモック実装（テスト用）
 *
 * ユニットテストで使用するモック実装です。
 * MockKなどのモッキングライブラリと組み合わせて使用します。
 *
 * 使用例:
 * ```kotlin
 * @Test
 * fun `タスク作成時にAnalyticsイベントが記録される`() = runTest {
 *     // Given
 *     val mockAnalyticsRepository = mockk<AnalyticsRepository>(relaxed = true)
 *     val viewModel = TaskViewModel(mockAnalyticsRepository)
 *
 *     // When
 *     viewModel.onTaskCreateClicked()
 *
 *     // Then
 *     coVerify {
 *         mockAnalyticsRepository.logEvent(
 *             eventName = AnalyticsEvents.TASK_CREATE_CLICKED,
 *             params = emptyMap()
 *         )
 *     }
 * }
 * ```
 *
 * 関連要件:
 * - NFR-202: システムはAnalyticsRepositoryのモック実装をテスト用に提供しなければならない
 */
class FakeAnalyticsRepository : AnalyticsRepository {

    /** 記録されたイベントのリスト */
    val loggedEvents = mutableListOf<LoggedEvent>()

    /** 設定されたユーザープロパティのリスト */
    val userProperties = mutableMapOf<String, String>()

    /** 記録された画面表示イベントのリスト */
    val screenViews = mutableListOf<String>()

    override suspend fun logEvent(
        eventName: String,
        params: Map<String, Any>
    ) {
        loggedEvents.add(LoggedEvent(eventName, params))
    }

    override suspend fun setUserProperty(
        name: String,
        value: String
    ) {
        userProperties[name] = value
    }

    override suspend fun logScreenView(
        screenName: String
    ) {
        screenViews.add(screenName)
    }

    /** 全てのログをクリア */
    fun clear() {
        loggedEvents.clear()
        userProperties.clear()
        screenViews.clear()
    }

    /** 記録されたイベント */
    data class LoggedEvent(
        val eventName: String,
        val params: Map<String, Any>
    )
}
