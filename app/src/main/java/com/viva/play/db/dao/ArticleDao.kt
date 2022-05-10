package com.viva.play.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.viva.play.db.entity.PoArticleEntity
import com.viva.play.service.Url

/**
 * @author 李雄厚
 *
 *
 */
@Dao
interface ArticleDao {

    @Insert
    suspend fun insert(data: List<PoArticleEntity>)

    @Query("UPDATE Article SET collect = :collect WHERE id = :id AND `key`=:key")
    fun collectQuestion(id: Int, collect: Boolean, key: String = Url.Question)

    @Query("SELECT * FROM Article WHERE `key`=:key")
    fun findArticle(key: String = Url.Question): PagingSource<Int, PoArticleEntity>

    @Query("DELETE FROM Article WHERE `key`=:key")
    suspend fun delete(key: String = Url.Question)

}