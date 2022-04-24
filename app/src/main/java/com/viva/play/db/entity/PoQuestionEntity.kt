package com.viva.play.db.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.viva.play.base.paging.BasePagingData
import com.viva.play.service.entity.DataEntity
import com.viva.play.service.entity.Tag

/**
 * @author 李雄厚
 *
 *
 */
@Entity(tableName = "Question", indices = [Index(value = arrayOf("id"), unique = true)])
data class PoQuestionEntity(
    val author: String,
    val chapterName: String,
    var collect: Boolean,
    val desc: String,
    val envelopePic: String,
    val fresh: Boolean,
    val id: Int,
    val link: String,
    val niceDate: String,
    val publishTime: Long,
    val shareUser: String,
    val superChapterName: String,
    val tags: List<Tag>?,
    val title: String,
    val type: Int,
    val userId: Int,
) : BasePagingData() {

    @PrimaryKey(autoGenerate = true)
    var primaryId = 0L


    companion object {
        fun parse(data: List<DataEntity>, page: Int): List<PoQuestionEntity> {
            return data.map {
                PoQuestionEntity(
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
                    userId = it.userId,
                ).apply {
                    this.page = page + 1
                }
            }
        }
    }
}