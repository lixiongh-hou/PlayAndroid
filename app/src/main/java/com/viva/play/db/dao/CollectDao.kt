package com.viva.play.db.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.viva.play.db.entity.PoCollectArticleEntity
import com.viva.play.db.entity.PoCollectLinkEntity
import com.viva.play.utils.CookieCache

/**
 * @author 李雄厚
 *
 *
 */
@Dao
interface CollectDao {

    @Insert
    suspend fun insertArticle(data: List<PoCollectArticleEntity>)

//    @Query("SELECT * FROM CollectArticle WHERE `key`=:key ORDER BY publishTime DESC LIMIT 0,20")
//    suspend fun findArticle(key: String = CookieCache.userId.toString()): List<PoCollectArticleEntity>

    @Query("SELECT * FROM CollectArticle WHERE `key`=:key ORDER BY publishTime DESC")
    fun findArticle(key: String = CookieCache.userId.toString()): PagingSource<Int, PoCollectArticleEntity>

    @Query("SELECT COUNT(id) AS total FROM CollectArticle WHERE `key`=:key")
    suspend fun findArticleTotal(key: String = CookieCache.userId.toString()): Int

    @Query("DELETE FROM CollectArticle WHERE `key`=:key")
    suspend fun deleteArticle(key: String = CookieCache.userId.toString())

    @Query("DELETE FROM CollectArticle WHERE `key`=:key AND id=:id AND originId=:originId")
    fun deleteArticle(id: Int, originId: Int, key: String = CookieCache.userId.toString())
    @Query("DELETE FROM CollectArticle WHERE `key`=:key AND originId=:id")
    fun deleteArticle(id: Int, key: String = CookieCache.userId.toString())


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLink(data: List<PoCollectLinkEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLink(data: PoCollectLinkEntity)

    @get:Query("SELECT * FROM CollectLink")
    val findLink: List<PoCollectLinkEntity>

    /**
     * 返回PagingSource，当数据发生改变时候都能监听到
     * 如[deleteLink] [updateLink]时候
     */
    @Query("SELECT * FROM CollectLink")
    fun findLinkList(): PagingSource<Int, PoCollectLinkEntity>

    @Query("SELECT COUNT(collectId) AS total FROM CollectLink LIMIT :offset, 20")
    suspend fun findLinkTotal(offset: Int): Int

    @Update
    fun updateLink(data: PoCollectLinkEntity)

    @Query("DELETE FROM CollectLink")
    fun deleteLink()

    @Query("DELETE FROM CollectLink WHERE collectId = :id")
    fun deleteLink(id: Int)
}