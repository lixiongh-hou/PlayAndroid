package com.viva.play.di

import android.content.Context
import com.viva.play.db.BaseDataBase
import com.viva.play.db.dao.HomeArticleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @author 李雄厚
 *
 *
 */
@InstallIn(SingletonComponent::class)
@Module
class DataBaseModel {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): BaseDataBase {
        return BaseDataBase.getInstance(context)
    }

}