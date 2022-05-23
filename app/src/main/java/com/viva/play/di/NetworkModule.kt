package com.viva.play.di

import com.viva.play.service.CommonService
import com.viva.play.service.Host
import com.viva.play.service.interceptor.LoadCookieInterceptor
import com.viva.play.service.interceptor.LoggingInterceptor
import com.viva.play.service.interceptor.SaveCookieInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class MadeInCommon

@Qualifier
annotation class MadeInPoetry

/**
 * @author 李雄厚
 *
 *
 */
@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    /**
     * @Provides 常用于被 @Module 注解标记类的内部的方法，并提供依赖项对象。
     * @Singleton 提供单例
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(SaveCookieInterceptor())
            .addInterceptor(LoadCookieInterceptor())
            .addInterceptor(
                LoggingInterceptor.Builder()
                    .isDebug(true)
                    .setRequestTag("请求")// Request
                    .setResponseTag("响应")// Response
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Content-Encoding", "gzip")
                    .build()
            )
            .build()
    }

    @MadeInCommon
    @Singleton
    @Provides
    fun provideCommonService(okHttpClient: OkHttpClient): CommonService {
        return Retrofit.Builder()
            .baseUrl(Host.HOST)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(CommonService::class.java)
    }

    @MadeInPoetry
    @Singleton
    @Provides
    fun providePoetryService(okHttpClient: OkHttpClient): CommonService {
        return Retrofit.Builder()
            .baseUrl("https://v2.jinrishici.com/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(CommonService::class.java)
    }
}