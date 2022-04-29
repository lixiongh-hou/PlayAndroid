package com.viva.play.db.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.viva.play.db.entity.PoReadLaterEntity

/**
 * @author 李雄厚
 *
 *
 */
@Dao
interface ReadLaterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: PoReadLaterEntity)

    @Query("DELETE FROM ReadLater WHERE link=:link")
    fun delete(link: String): Int

    @Query("SELECT * FROM ReadLater ORDER BY time DESC")
    fun findReadLaterList(): PagingSource<Int, PoReadLaterEntity>
    @Query("SELECT * FROM ReadLater ORDER BY time DESC")
    fun findReadLaterListLiveData(): LiveData<List<PoReadLaterEntity>>

    @Query("SELECT * FROM ReadLater WHERE link=:link")
    fun isReadLater(link: String): PoReadLaterEntity?

    @Query("DELETE FROM ReadLater")
    fun delete()

}