package com.viva.play.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.viva.play.db.converters.HomeArticleConverters
import com.viva.play.db.dao.CollectDao
import com.viva.play.db.dao.HomeArticleDao
import com.viva.play.db.dao.PoUserInfoDao
import com.viva.play.db.dao.QuestionDao
import com.viva.play.db.entity.*

/**
 * @author 李雄厚
 *
 *
 */
@Database(
    entities = [
        PoArticleEntity::class,
        PoBannerEntity::class,
        PoUserInfo::class,
        PoCollectLinkEntity::class,
        PoCollectArticleEntity::class,
        PoQuestionEntity::class
    ],

    version = 1,
    exportSchema = false
)
@TypeConverters(value = [HomeArticleConverters::class])
abstract class BaseDataBase : RoomDatabase() {

    abstract fun homeArticleDao(): HomeArticleDao

    abstract fun userInfoDao(): PoUserInfoDao

    abstract fun collectDao(): CollectDao

    abstract fun questionDao(): QuestionDao


    companion object {
        @Volatile
        private var INSTANCE: BaseDataBase? = null
        fun getInstance(context: Context): BaseDataBase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context,
                BaseDataBase::class.java, "playAndroid.db"
            )
//                .fallbackToDestructiveMigration()
                .build()
    }
}