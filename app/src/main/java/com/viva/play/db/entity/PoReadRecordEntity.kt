package com.viva.play.db.entity

import androidx.annotation.IntRange
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * @author 李雄厚
 *
 * 这里类和[PoBookDetailsEntity]类绑定，用于区分阅读多少了
 */
@Entity(tableName = "ReadRecord")
data class PoReadRecordEntity(
    @PrimaryKey
    val id: Int,
    val author: String,
    val userId: Int,
    val link: String,
    val title: String,
    val lastTime: Date?,
    @IntRange(from = 0, to = 10000) val percent: Int
) {

}