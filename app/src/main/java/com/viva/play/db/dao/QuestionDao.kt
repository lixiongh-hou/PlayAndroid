package com.viva.play.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.viva.play.db.entity.PoQuestionEntity

/**
 * @author 李雄厚
 *
 *
 */
@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: List<PoQuestionEntity>)

    @Query("SELECT * FROM Question")
    fun findQuestion(): PagingSource<Int, PoQuestionEntity>

    @Query("UPDATE Question SET collect = :collect WHERE id = :id")
    fun collectQuestion(id: Int, collect: Boolean)

    @Query("DELETE FROM Question")
    suspend fun delete()
}