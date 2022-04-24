package com.viva.play.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.viva.play.service.entity.CollectLinkEntity

/**
 * @author 李雄厚
 *
 * 收藏网址数据
 */
@Entity(tableName = "CollectLink")
data class PoCollectLinkEntity(
    @PrimaryKey
    val collectId: Int = -1,
    val title: String = "",
    val url: String = ""
) {
    companion object {
        fun parse(data: List<CollectLinkEntity>): List<PoCollectLinkEntity> {
            return data.map {
                PoCollectLinkEntity(it.id, it.name, it.link)
            }
        }

        fun parse(data: CollectLinkEntity): PoCollectLinkEntity {
            return PoCollectLinkEntity(data.id, data.name, data.link)
        }
    }
}