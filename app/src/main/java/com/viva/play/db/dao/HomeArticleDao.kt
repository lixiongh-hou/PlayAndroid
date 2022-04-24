package com.viva.play.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.viva.play.db.entity.PoArticleEntity
import com.viva.play.db.entity.PoBannerEntity

/**
 * @author 李雄厚
 *
 *
 */
@Dao
interface HomeArticleDao {

    @Insert
    fun insert(data: List<PoArticleEntity>)

    /**
     * 查询类型总数
     *
     * @param type 1是置顶文章， 0是首页文章
     */
    @Query("SELECT COUNT(id) AS total FROM HomeArticle WHERE type = :type ORDER BY publishTime DESC")
    fun findTypeTotal(type: Int): Int

    /**
     * 查询文章数据
     *
     * @param type 1是置顶文章， 0是首页文章
     */
    @Query("SELECT * FROM HomeArticle WHERE type = :type ORDER BY publishTime DESC")
    fun findArticle(type: Int): List<PoArticleEntity>?

    /**
     * 收藏
     *
     * @param collect true收藏  false取消收藏
     */
    @Query("UPDATE HomeArticle SET collect = :collect WHERE id = :id")
    fun collectArticle(id: Int, collect: Boolean)

    /**
     * 删除文章数据
     *
     * @param type 1是置顶文章， 0是首页文章
     */
    @Query("DELETE FROM HomeArticle WHERE  type = :type")
    fun deleteArticle(type: Int)

    /*------------------------------下方是Banner数据操作---------------------------------*/

    @Insert
    fun insertBanner(data: List<PoBannerEntity>)

    /**
     * 查询Banner总数
     */
    @get:Query("SELECT COUNT(id) AS total FROM Banner")
    val findBannerTotal: Int

    /**
     * 查询Banner数据
     */
    @get:Query("SELECT * FROM Banner")
    val findBanner: List<PoBannerEntity>


    /**
     * 删除Banner数据
     */
    @Query("DELETE FROM Banner")
    fun deleteBanner()
}