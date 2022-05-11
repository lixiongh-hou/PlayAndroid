package com.viva.play.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.viva.play.db.entity.PoBookDetailsEntity
import com.viva.play.db.entity.PoReadRecordEntity
import com.viva.play.service.Url
import java.util.*

/**
 * @author 李雄厚
 *
 *
 */
@Dao
interface BookDetailsDao {

    @Insert
    suspend fun insert(data: List<PoBookDetailsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReadRecord(data: PoReadRecordEntity)

    @Query("SELECT * FROM BookDetails WHERE `key`=:key")
    fun findBookDetails(key: String): PagingSource<Int, PoBookDetailsEntity>

    @Query("DELETE FROM BookDetails WHERE `key`=:key")
    suspend fun delete(key: String)

    @Query("SELECT * FROM ReadRecord WHERE link=:link AND`key`=:key")
    suspend fun findReadRecord(link: String, key: String): PoReadRecordEntity?

    @Query("SELECT * FROM ReadRecord WHERE link=:link AND`key`=:key")
    fun findReadRecord1(link: String, key: String): PoReadRecordEntity?

    @Query("UPDATE BookDetails SET percent=:percent WHERE `key`=:key AND link=:link AND percent < :percent")
    fun updateBookDetails(key: String, link: String, percent: Int)

    @Query("UPDATE BookDetails SET lastTime=:lastTime WHERE `key`=:key AND link=:link")
    fun updateBookDetailsTime(key: String, link: String, lastTime: Date)

    @Query("UPDATE ReadRecord SET percent=:percent WHERE `key`=:key AND link=:link AND percent < :percent")
    fun updateReadRecord(key: String, link: String, percent: Int)

    @Query("UPDATE ReadRecord SET lastTime=:lastTime WHERE `key`=:key AND link=:link")
    fun updateReadRecordTime(key: String, link: String, lastTime: Date)
}