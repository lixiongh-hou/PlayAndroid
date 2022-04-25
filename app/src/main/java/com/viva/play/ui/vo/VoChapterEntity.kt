package com.viva.play.ui.vo

import androidx.room.Embedded
import androidx.room.Relation
import com.viva.play.db.entity.PoChapterChildrenEntity
import com.viva.play.db.entity.PoChapterEntity
import com.viva.play.db.entity.Status
import com.viva.play.service.entity.ChapterEntity
import com.viva.play.service.entity.NaviEntity

/**
 * @author 李雄厚
 *
 * Vo实体类代表当需要统一数据格式用的，如果网络实体类和数据库实体类能用就不要新建
 */
data class VoChapterEntity(
    @Embedded
    val chapter: PoChapterEntity,
    @Relation(parentColumn = "id", entityColumn = "chapterId")
    val children: List<PoChapterChildrenEntity>
) {
    companion object {
        fun parseKnowledge(data: List<ChapterEntity>): List<VoChapterEntity> {
            return data.mapIndexed { index, chapterEntity ->
                VoChapterEntity(
                    chapter = PoChapterEntity(
                        chapterEntity.id,
                        chapterEntity.courseId,
                        chapterEntity.name,
                        chapterEntity.order,
                        chapterEntity.parentChapterId,
                        chapterEntity.visible,
                        Status.KNOWLEDGE,
                        index
                    ),
                    children = chapterEntity.children.mapIndexed { index1, children ->
                        PoChapterChildrenEntity(
                            children.id,
                            chapterEntity.id,
                            children.courseId,
                            children.name,
                            children.order,
                            children.parentChapterId,
                            children.visible,
                            index1
                        )
                    }
                )
            }
        }

        fun parseNavi(data: List<NaviEntity>): List<VoChapterEntity> {
            return data.mapIndexed { index, naviEntity ->
                VoChapterEntity(
                    chapter = PoChapterEntity(
                        naviEntity.cid, 0, naviEntity.name, 0, 0, 0, Status.NAVI, index
                    ),
                    children = naviEntity.articles.mapIndexed { index1, dataEntity ->
                        PoChapterChildrenEntity(
                            dataEntity.id,
                            naviEntity.cid,
                            dataEntity.courseId,
                            dataEntity.title,
                            0,
                            0,
                            dataEntity.visible,
                            index1
                        )
                    }
                )
            }
        }
    }
}