package com.viva.play.db.entity

import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.viva.play.base.paging.BasePagingData
import com.viva.play.service.entity.DataEntity
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
    val id: Int,
    val author: String,
    val userId: Int,
    val link: String,
    val title: String,
    val publishTime: Long,
    val lastTime: Date?,
    @IntRange(from = 0, to = 10000) val percent: Int
) : BasePagingData() {

    companion object {
        const val MAX_PERCENT = 10000
        fun parse(
            data: List<DataEntity>,
            readRecord: List<PoReadRecordEntity>,
            page: Int,
            key: String
        ): List<PoBookDetailsEntity> {
            return data.mapIndexed { index, dataEntity ->
                PoBookDetailsEntity(
                    key = key,
                    id = dataEntity.id,
                    author = dataEntity.author,
                    userId = dataEntity.userId,
                    link = readRecord[index].link,
                    title = dataEntity.title,
                    publishTime = dataEntity.publishTime,
                    lastTime = readRecord[index].lastTime,
                    percent = readRecord[index].percent
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