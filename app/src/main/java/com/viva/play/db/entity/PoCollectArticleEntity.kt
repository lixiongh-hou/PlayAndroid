package com.viva.play.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.viva.play.base.paging.BasePagingData
import com.viva.play.service.entity.CollectArticleEntity
import com.viva.play.utils.CookieCache

/**
 * @author 李雄厚
 *
 * 收藏站内文章数据 只会缓存前20条数据
 */
@Entity(tableName = "CollectArticle")
data class PoCollectArticleEntity(
    /**
     * 用户key，就是用户的userName
     */
    val key: String,
    val id: Int,
    val author: String,
    val chapterName: String,
    val desc: String,
    val envelopePic: String,
    val link: String,
    val niceDate: String,
    val origin: String,
    val originId: Int,
    val publishTime: Long,
    val title: String,
    val userId: Int
) : BasePagingData() {

    @PrimaryKey(autoGenerate = true)
    var primaryId: Long = 0

    companion object {
        fun parse(data: CollectArticleEntity, page: Int): List<PoCollectArticleEntity> {
            return data.data.map {
                PoCollectArticleEntity(
                    CookieCache.userId.toString(),
                    it.id,
                    it.author,
                    it.chapterName,
                    it.desc,
                    it.envelopePic,
                    it.link,
                    it.niceDate,
                    it.origin,
                    it.originId,
                    it.publishTime,
                    it.title,
                    it.userId
                ).apply {
                    this.page = page + 1
                }
            }
        }
    }
}