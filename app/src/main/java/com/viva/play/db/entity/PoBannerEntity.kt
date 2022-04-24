package com.viva.play.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.viva.play.service.entity.BannerEntity

/**
 * @author 李雄厚
 *
 *
 */
@Entity(tableName = "Banner")
data class PoBannerEntity(
    val desc: String,
    val id: Int,
    val imagePath: String,
    val isVisible: Int,
    val order: Int,
    val title: String,
    val type: Int,
    val url: String
) {
    @PrimaryKey(autoGenerate = true)
    var primaryId: Long = 0L

    companion object {
        fun parse(data: List<BannerEntity>): List<PoBannerEntity> {
            return data.map {
                PoBannerEntity(
                    it.desc,
                    it.id,
                    it.imagePath,
                    it.isVisible,
                    it.order,
                    it.title,
                    it.type,
                    it.url
                )
            }
        }
    }
}