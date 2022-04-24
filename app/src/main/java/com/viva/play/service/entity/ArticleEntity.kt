package com.viva.play.service.entity

import com.google.gson.annotations.SerializedName

/**
 * @author 李雄厚
 *
 *
 */
data class ArticleEntity(
    @SerializedName("datas")
    val data: MutableList<DataEntity>,
    val over: Boolean
)