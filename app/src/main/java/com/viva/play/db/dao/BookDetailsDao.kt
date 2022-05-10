package com.viva.play.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.viva.play.db.entity.PoBookDetailsEntity
import com.viva.play.db.entity.PoReadRecordEntity
import com.viva.play.service.Url

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
}