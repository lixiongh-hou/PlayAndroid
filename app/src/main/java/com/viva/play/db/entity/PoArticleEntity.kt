package com.viva.play.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.viva.play.base.paging.BasePagingData
import com.viva.play.service.entity.DataEntity
import com.viva.play.service.entity.Tag

/**
 * @author 李雄厚
 *
 * 统一文章列表的本地数据,对应网络数据[DataEntity]，除了首页数据另外处理
 */
@Entity(tableName = "Article")
data class PoArticleEntity(
    /**
     * 很多接口的返回值都的实体类都是一样的做一个区分来判断是那个接口的数据
     */
    val key: String,
    val author: String,
    val collect: Boolean,
    val desc: String,
    val envelopePic: String,
    val id: Int,
    val link: String,
    val shareUser: String,
    val niceDate: String,
    val publishTime: Long,
    val title: String,
    val userId: Int = 0,
    //不为null显示，否则隐藏
    val tags: List<Tag>?,
    //是否是置顶 1显示置顶显示，否则隐藏
    val type: Int,
    //是否是新发布 true显示，否则隐藏
    val fresh: Boolean,
    //这两个字段不为空显示，否则隐藏
    val superChapterName: String,
    val chapterName: String,
) : BasePagingData() {

    @PrimaryKey(autoGenerate = true)
    var primaryId = 0L


    companion object {
        /**
         * @param page 本地缓存需要填入
         * @param key 本地缓存需要填入
         */
        fun parse(data: List<DataEntity>, page: Int, key: String): List<PoArticleEntity> {
            return data.map {
                PoArticleEntity(
                    key = key,
                    userId = it.userId,
                    author = it.author,
                    chapterName = it.chapterName,
                    collect = it.collect,
                    desc = it.desc,
                    envelopePic = it.envelopePic,
                    fresh = it.fresh,
                    id = it.id,
                    link = it.link,
                    niceDate = it.niceDate,
                    publishTime = it.publishTime,
                    shareUser = it.shareUser,
                    superChapterName = it.superChapterName,
                    tags = it.tags,
                    title = it.title,
                    type = it.type,
                ).apply {
                    this.page = page + 1
                }
            }
        }
    }
}