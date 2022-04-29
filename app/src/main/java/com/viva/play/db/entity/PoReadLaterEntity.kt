package com.viva.play.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.viva.play.base.paging.BasePagingData
import java.util.*

/**
 * @author 李雄厚
 *
 *
 */
@Entity(tableName = "ReadLater")
data class PoReadLaterEntity(
    @PrimaryKey
    val link: String,
    val title: String,
    val time: Date = Date()
) : BasePagingData() {
}