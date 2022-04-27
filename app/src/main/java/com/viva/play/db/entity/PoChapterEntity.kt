package com.viva.play.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.viva.play.service.entity.ChapterEntity
import com.viva.play.service.entity.NaviEntity
import java.io.Serializable

/**
 * @author 李雄厚
 *
 *
 */
@Entity(tableName = "Chapter")
data class PoChapterEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val status: Status = Status.KNOWLEDGE,
    /**
     * 用来进行排序的
     */
    val index: Int

): Serializable {
    companion object {
        fun parseKnowledge(data: List<ChapterEntity>): List<PoChapterEntity> {
            return data.mapIndexed { index, chapterEntity ->
                PoChapterEntity(
                    chapterEntity.id,
                    chapterEntity.name,
                    Status.KNOWLEDGE,
                    index,
                )
            }
        }

        fun parseNavi(data: List<NaviEntity>): List<PoChapterEntity> {
            return data.mapIndexed { index, naviEntity ->
                PoChapterEntity(naviEntity.cid, naviEntity.name, Status.NAVI, index)
            }
        }
    }
}

enum class Status(val type: Int) {
    /**
     * 取出体系数据
     */
    KNOWLEDGE(0),

    /**
     * 取出导航数据
     */
    NAVI(1)
}