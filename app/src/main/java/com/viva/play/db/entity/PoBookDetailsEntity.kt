package com.viva.play.db.entity

import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.viva.play.base.paging.BasePagingData
import java.util.*

/**
 * @author 李雄厚
 *
 *
 */
@Entity(tableName = "BookDetails")
data class PoBookDetailsEntity(
    /**
     * 很多接口的返回值都的实体类都是一样的做一个区分来判断是那个接口的数据
     */
    val key: String,
    val link: String,
    val title: String,
    val publishTime: Long,
    val lastTime: Date?,
    @IntRange(from = 0, to = 10000) val percent: Int
) : BasePagingData() {

    companion object {
        const val MAX_PERCENT = 10000
        fun parse(
            readRecord: List<PoReadRecordEntity>,
            page: Int,
            key: String
        ): List<PoBookDetailsEntity> {
            return readRecord.map {
                PoBookDetailsEntity(
                    key = key,
                    link = it.link,
                    title = it.title,
                    publishTime = it.publishTime,
                    lastTime = it.lastTime,
                    percent = it.percent
                ).apply {
                    this.page = page + 1
                }
            }
        }
    }

    @PrimaryKey(autoGenerate = true)
    var primaryId: Long = 0L

    val percentFloat: Float
        @FloatRange(from = 0.0, to = 1.0)
        get() = (percent.toFloat() / MAX_PERCENT.toFloat()).coerceIn(0f, 1f)
}