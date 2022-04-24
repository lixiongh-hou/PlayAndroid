package com.viva.play.di

import com.viva.play.service.CommonService
import com.viva.play.service.HttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
class NetworkModule {

    @MadeInCommon
    @Singleton
    @Provides
    fun provideCommonService(): CommonService {
        return HttpClient.commonService
    }

    @MadeInPoetry
    @Singleton
    @Provides
    fun providePoetryService(): CommonService {
        return HttpClient.poetryService
    }
}