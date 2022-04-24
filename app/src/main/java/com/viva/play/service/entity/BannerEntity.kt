package com.viva.play.service.entity

/**
 * @author 李雄厚
 *
 *
 */
data class BannerEntity(
    /**
    "desc": "我们支持订阅啦~",
    "id": 30,
    "imagePath": "https://www.wanandroid.com/blogimgs/42da12d8-de56-4439-b40c-eab66c227a4b.png",
    "isVisible": 1,
    "order": 0,
    "title": "我们支持订阅啦~",
    "type": 0,
    "url": "https://www.wanandroid.com/blog/show/3161"
     */
    val desc: String,
    val id: Int,
    val imagePath: String,
    val isVisible: Int,
    val order: Int,
    val title: String,
    val type: Int,
    val url: String
)