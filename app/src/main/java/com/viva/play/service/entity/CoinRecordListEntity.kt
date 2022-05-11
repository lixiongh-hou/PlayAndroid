package com.viva.play.service.entity

import com.google.gson.annotations.SerializedName

/**
 * @author 李雄厚
 *
 *
 */
data class CoinRecordListEntity(
    val over: Boolean,
    @SerializedName("datas")
    val data: List<CoinRecord>
)

data class CoinRecord(
    val coinCount: Int,
    val date: Long,
    val desc: String,
    val id: Int,
    val type: Int,
    val userId: Int,
    val userName: String

)