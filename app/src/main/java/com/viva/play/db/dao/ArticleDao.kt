package com.viva.play.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.viva.play.db.entity.PoArticleEntity

/**
 * @author 李雄厚
 *
 *
 */
@Dao
interface ArticleDao {

    @Insert
    suspend fun insert(data: List<PoArticleEntity>)

    @Query("SELECT * FROM Article")
    fun findQuestion(): PagingSource<Int, PoArticleEntity>

    @Query("UPDATE Article SET collect = :collect WHERE id = :id")
    fun collectQuestion(id: Int, collect: Boolean)

    @Query("DELETE FROM Article")
    suspend fun delete()
}