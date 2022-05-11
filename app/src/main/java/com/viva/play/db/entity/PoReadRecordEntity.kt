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
    /**
     * 很多接口的返回值都的实体类都是一样的做一个区分来判断是那个接口的数据
     */
    val key: String,
    @PrimaryKey
    val link: String,
    val lastTime: Date?,
    @IntRange(from = 0, to = 10000) val percent: Int
) {

}