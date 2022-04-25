package com.viva.play.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.viva.play.db.converters.HomeArticleConverters
import com.viva.play.db.dao.*
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
        PoQuestionEntity::class,
        PoChapterEntity::class,
        PoChapterChildrenEntity::class
    ],

    version = 3,
    exportSchema = false
)
@TypeConverters(value = [HomeArticleConverters::class])
abstract class BaseDataBase : RoomDatabase() {

    abstract fun homeArticleDao(): HomeArticleDao

    abstract fun userInfoDao(): PoUserInfoDao

    abstract fun collectDao(): CollectDao

    abstract fun questionDao(): QuestionDao

    abstract fun chapterDao(): ChapterDao


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
                .addMigrations(MIGRATION_1_2)
                .addMigrations(MIGRATION_2_3)
                .build()

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                //创建[Chapter]表
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `Chapter` (`id` INTEGER NOT NULL DEFAULT 0, `courseId` INTEGER NOT NULL DEFAULT 0, `name` TEXT NOT NULL DEFAULT '', `order` INTEGER NOT NULL DEFAULT 0, `parentChapterId` INTEGER NOT NULL DEFAULT 0, `visible` INTEGER NOT NULL DEFAULT 0, PRIMARY KEY(`id`))"
                )
                //创建[ChapterChildren]表，并且设置外键
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `ChapterChildren` (`id` INTEGER NOT NULL DEFAULT 0, `chapterId` INTEGER NOT NULL DEFAULT 0, `courseId` INTEGER NOT NULL DEFAULT 0, `name` TEXT NOT NULL DEFAULT '', `order` INTEGER NOT NULL DEFAULT 0, `parentChapterId` INTEGER NOT NULL DEFAULT 0, `visible` INTEGER NOT NULL DEFAULT 0, PRIMARY KEY(`id`), FOREIGN KEY(`chapterId`) REFERENCES `Chapter`(`id`) ON UPDATE CASCADE ON DELETE CASCADE)"
                )
                //为[ChapterChildren]表中[chapterId]字段添加索引
                database.execSQL("CREATE INDEX IF NOT EXISTS index_chapterId ON ChapterChildren (chapterId)")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE Chapter ADD COLUMN status INTEGER NOT NULL DEFAULT 0"
                )
            }

        }

    }


}