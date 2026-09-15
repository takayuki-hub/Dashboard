package com.github.takayuki_hub.dashboard

import android.app.Application
import android.util.Log
import androidx.core.os.bundleOf
import com.github.takayuki_hub.dashboard.core.analytics.constants.AnalyticsEvents
import com.github.takayuki_hub.dashboard.core.analytics.constants.AnalyticsParams
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {

    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate() {
        super.onCreate()

        // デバッグビルド時はFirebase Analyticsを無効化
        if (BuildConfig.DEBUG) {
            firebaseAnalytics.setAnalyticsCollectionEnabled(false)
            Log.d(TAG, "Firebase Analytics disabled in debug build")
        } else {
            firebaseAnalytics.setAnalyticsCollectionEnabled(true)
            Log.d(TAG, "Firebase Analytics enabled in release build")

            // アプリ起動イベントを記録
            firebaseAnalytics.logEvent(
                AnalyticsEvents.APP_LAUNCHED,
                bundleOf(
                    AnalyticsParams.APP_VERSION to BuildConfig.VERSION_NAME
                )
            )

            // アプリバージョンをユーザープロパティとして設定
            firebaseAnalytics.setUserProperty(
                AnalyticsParams.APP_VERSION,
                BuildConfig.VERSION_NAME
            )
        }
    }

    companion object {
        private const val TAG = "MainApplication"
    }
}