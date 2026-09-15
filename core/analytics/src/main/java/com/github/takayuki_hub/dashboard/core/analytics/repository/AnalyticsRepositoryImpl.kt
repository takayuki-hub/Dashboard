package com.github.takayuki_hub.dashboard.core.analytics.repository

import android.util.Log
import androidx.core.os.bundleOf
import com.github.takayuki_hub.dashboard.core.analytics.constants.AnalyticsParams
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

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
            val bundle = bundleOf(*limitedParams.map { (key, value) ->
                key to when (value) {
                    is String -> value
                    is Int -> value
                    is Long -> value
                    is Double -> value
                    is Boolean -> value
                    else -> value.toString()
                }
            }.toTypedArray())

            firebaseAnalytics.logEvent(eventName, bundle)
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
            val bundle = bundleOf(
                AnalyticsParams.SCREEN_NAME to screenName,
                AnalyticsParams.SCREEN_CLASS to screenName
            )
            firebaseAnalytics.logEvent(
                FirebaseAnalytics.Event.SCREEN_VIEW,
                bundle
            )
        }
    }

    companion object {
        private const val TAG = "AnalyticsRepository"
    }
}
