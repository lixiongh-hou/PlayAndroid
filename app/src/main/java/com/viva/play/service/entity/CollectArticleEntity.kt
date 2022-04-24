package com.viva.play.service.entity

import com.google.gson.annotations.SerializedName

/**
 * @author 李雄厚
 *
 *
 */
data class CollectArticleEntity(

    /**
    "author": "扔物线",
    "chapterId": 249,
    "chapterName": "干货资源",
    "courseId": 13,
    "desc": "",
    "envelopePic": "",
    "id": 253458,
    "link": "https://rengwuxian.com/jetpack-compose-animation/",
    "niceDate": "20小时前",
    "origin": "",
    "originId": 12554,
    "publishTime": 1650262964000,
    "title": "属性动画为什么不能移植到 Jetpack Compose？",
    "userId": 72836,
    "visible": 0,
    "zan": 0
     */
    @SerializedName("datas")
    val data: List<CollectData>,
    val over: Boolean
)

data class CollectData(
    val author: String,
    val chapterId: Int,
    val chapterName: String,
    val courseId: Int,
    val desc: String,
    val envelopePic: String,
    val id: Int,
    val link: String,
    val niceDate: String,
    val origin: String,
    val originId: Int,
    val publishTime: Long,
    val title: String,
    val userId: Int,
    val visible: Int,
    val zan: Int
)