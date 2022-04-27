package com.viva.play.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.viva.play.service.entity.Children
import com.viva.play.service.entity.DataEntity
import java.io.Serializable

/**
 * @author 李雄厚
 *
 *
 */
@Entity(
    tableName = "ChapterChildren",
    foreignKeys = [ForeignKey(
        entity = PoChapterEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("chapterId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )],
    indices = [Index(value = arrayOf("chapterId"))]
)
data class PoChapterChildrenEntity(
    @PrimaryKey
    val id: Int,
    /**
     * 这个是外键id
     */
    val chapterId: Int,
    val name: String,
    /**
     * 用来进行排序的
     */
    val index: Int,
): Serializable {
    /**
     * 下方数据是导航需要的
     */
    var link: String = ""
    var author: String = ""
    var collected: Boolean = false
    var userId: Int = 0

    companion object {
        fun parseKnowledge(data: List<Children>, chapterId: Int): List<PoChapterChildrenEntity> {
            return data.mapIndexed { index, children ->
                PoChapterChildrenEntity(
                    children.id,
                    chapterId,
                    children.name,
                    index
                )
            }
        }

        fun parseNavi(data: List<DataEntity>, chapterId: Int): List<PoChapterChildrenEntity> {
            return data.mapIndexed { index, dataEntity ->
                PoChapterChildrenEntity(
                    dataEntity.id,
                    chapterId,
                    dataEntity.title,
                    index
                ).apply {
                    link = dataEntity.link
                    author = dataEntity.author
                    collected = dataEntity.collect
                    userId = dataEntity.userId
                }
            }
        }
    }
}