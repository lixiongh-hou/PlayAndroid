package com.viva.play.db.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.viva.play.service.entity.DataEntity
import com.viva.play.service.entity.Tag

/**
 * @author 李雄厚
 *
 * 首页文章列表，只会缓存置顶文章和前20条数据
 */
@Entity(tableName = "HomeArticle")
data class PoArticleEntity(
    val author: String,
    val chapterId: Int,
    val chapterName: String,
    var collect: Boolean,
    val courseId: Int,
    val desc: String,
    val envelopePic: String,
    val fresh: Boolean,
    val id: Int,
    val link: String,
    val niceDate: String,
    val niceShareDate: String,
    val projectLink: String,
    val publishTime: Long,
    val realSuperChapterId: Int,
    val selfVisible: Int,
    val shareDate: Long,
    val shareUser: String,
    val superChapterId: Int,
    val superChapterName: String,
    val tags: List<Tag>?,
    val title: String,
    val type: Int,
    val userId: Int,
) {

    @PrimaryKey(autoGenerate = true)
    var primaryId: Long = 0L

    //把banner图放进Rv里面，第一个Position，不需要存储到本地数据库
    @Ignore
    var banner: List<PoBannerEntity>? = null

    companion object {
        fun parse(data: List<DataEntity>): List<PoArticleEntity> {
            return data.map {
                PoArticleEntity(
                    author = it.author,
                    chapterId = it.chapterId,
                    chapterName = it.chapterName,
                    collect = it.collect,
                    courseId = it.courseId,
                    desc = it.desc,
                    envelopePic = it.envelopePic,
                    fresh = it.fresh,
                    id = it.id,
                    link = it.link,
                    niceDate = it.niceDate,
                    niceShareDate = it.niceShareDate,
                    projectLink = it.projectLink,
                    publishTime = it.publishTime,
                    realSuperChapterId = it.realSuperChapterId,
                    selfVisible = it.selfVisible,
                    shareDate = it.shareDate,
                    shareUser = it.shareUser,
                    superChapterId = it.superChapterId,
                    superChapterName = it.superChapterName,
                    tags = it.tags,
                    title = it.title,
                    type = it.type,
                    userId = it.userId
                )
            }
        }
    }
}

