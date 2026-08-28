package com.github.takayuki_hub.dashboard.core.network.di

import com.github.takayuki_hub.dashboard.core.network.api.NewsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named
import jakarta.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NewsNetworkModule {

    @Provides
    @Singleton
    @Named("NewsRetrofit")
    fun provideNewsRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://newsapi.org/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideNewsApi(@Named("NewsRetrofit") retrofit: Retrofit): NewsApi {
        return retrofit.create(NewsApi::class.java)
    }
}