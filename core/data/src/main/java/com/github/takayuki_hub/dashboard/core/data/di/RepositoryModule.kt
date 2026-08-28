package com.github.takayuki_hub.dashboard.core.data.di

import com.github.takayuki_hub.dashboard.core.data.repository.NewsRepository
import com.github.takayuki_hub.dashboard.core.data.repository.NewsRepositoryImpl
import com.github.takayuki_hub.dashboard.core.data.repository.TaskRepository
import com.github.takayuki_hub.dashboard.core.data.repository.TaskRepositoryImpl
import com.github.takayuki_hub.dashboard.core.data.repository.WeatherRepository
import com.github.takayuki_hub.dashboard.core.data.repository.WeatherRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // アプリ全体（SingletonScope）で保持・使用する設定
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        impl: WeatherRepositoryImpl // 実際に作成するインスタンス（入力）
    ): WeatherRepository           // 要求されているインターフェース（出力）

    @Binds
    @Singleton
    abstract fun bindNewsRepository(
        newsRepositoryImpl: NewsRepositoryImpl
    ): NewsRepository

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        taskRepositoryImpl: TaskRepositoryImpl
    ): TaskRepository
}