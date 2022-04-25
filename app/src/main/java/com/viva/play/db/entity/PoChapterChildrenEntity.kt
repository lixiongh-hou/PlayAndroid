package com.viva.play.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.viva.play.service.entity.Children
import com.viva.play.service.entity.DataEntity
import com.viva.play.service.entity.NaviEntity

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
    val chapterId: Int,
    val courseId: Int,
    val name: String,
    val order: Int,
    val parentChapterId: Int,
    val visible: Int,
    /**
     * 用来进行排序的
     */
    val index: Int
) {
    companion object {
        fun parseKnowledge(data: List<Children>, chapterId: Int): List<PoChapterChildrenEntity> {
            return data.mapIndexed { index, children ->
                PoChapterChildrenEntity(
                    children.id,
                    chapterId,
                    children.courseId,
                    children.name,
                    children.order,
                    children.parentChapterId,
                    children.visible,
                    index
                )
            }
        }

        fun parseNavi(data: List<DataEntity>, chapterId: Int): List<PoChapterChildrenEntity> {
            return data.mapIndexed { index, dataEntity ->
                PoChapterChildrenEntity(
                    dataEntity.id,
                    chapterId,
                    dataEntity.courseId,
                    dataEntity.title,
                    0,
                    0,
                    dataEntity.visible,
                    index
                )
            }
        }
    }
}