package com.viva.play.db.dao

import androidx.paging.PagingSource
import androidx.room.*
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

    @Query("UPDATE BookDetails SET percent=:percent, lastTime=:lastTime WHERE `key`=:key AND link=:link AND percent < :percent")
    fun updateBookDetails(key: String, link: String, percent: Int, lastTime: Date)

    @Query("UPDATE BookDetails SET lastTime=:lastTime WHERE `key`=:key AND link=:link")
    fun updateBookDetailsTime(key: String, link: String, lastTime: Date)


    @Query("SELECT * FROM ReadRecord WHERE link=:link")
    suspend fun findReadRecord(link: String): PoReadRecordEntity?

    @Query("SELECT * FROM ReadRecord WHERE link=:link AND id=:id")
    fun findReadRecord1(id: Int, link: String): PoReadRecordEntity?

    @Query("UPDATE ReadRecord SET percent=:percent, lastTime=:lastTime WHERE link=:link AND id=:id AND percent < :percent")
    fun updateReadRecord(id: Int, link: String, percent: Int, lastTime: Date)

    @Query("UPDATE ReadRecord SET lastTime=:lastTime WHERE link=:link AND id=:id")
    fun updateReadRecordTime(id: Int, link: String, lastTime: Date)

    @Query("SELECT * FROM ReadRecord ORDER BY lastTime DESC")
    fun findReadRecord(): PagingSource<Int, PoReadRecordEntity>

    @Delete
    fun delReadRecord(data: PoReadRecordEntity)

    @Query("DELETE FROM ReadRecord")
    fun delAllReadRecord()
}