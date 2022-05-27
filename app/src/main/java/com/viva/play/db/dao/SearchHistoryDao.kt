package com.viva.play.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.viva.play.db.entity.PoHotKeyEntity
import com.viva.play.db.entity.PoSearchHistoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * @author 李雄厚
 *
 *
 */
@Dao
interface SearchHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistory(data: PoSearchHistoryEntity)

    @get:Query("SELECT * FROM SearchHistory ORDER BY time DESC")
    val findSearchHistory: List<PoSearchHistoryEntity>

    @Delete
    fun delete(data: PoSearchHistoryEntity)

    @Query("DELETE FROM SearchHistory")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHotKey(data: List<PoHotKeyEntity>)

    @Query("SELECT * FROM HotKey")
    fun findHotKey(): Flow<List<PoHotKeyEntity>>

    @Query("SELECT COUNT(*) AS total FROM HotKey")
    suspend fun findTotal(): Int

}