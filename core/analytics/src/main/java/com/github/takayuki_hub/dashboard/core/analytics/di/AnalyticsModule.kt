package com.github.takayuki_hub.dashboard.core.analytics.di

import android.content.Context
import com.github.takayuki_hub.dashboard.core.analytics.repository.AnalyticsRepository
import com.github.takayuki_hub.dashboard.core.analytics.repository.AnalyticsRepositoryImpl
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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
