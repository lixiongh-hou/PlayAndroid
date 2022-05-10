package com.viva.play.service.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author 李雄厚
 *
 *
 */
@Parcelize
data class BookEntity(

    /**
    "author": "阮一峰",
    "children": [],
    "courseId": 13,
    "cover": "https://www.wanandroid.com/blogimgs/f1cb8d34-82c1-46f7-80fe-b899f56b69c1.png",
    "desc": "C 语言入门教程。",
    "id": 548,
    "lisense": "知识共享 署名-相同方式共享 3.0协议",
    "lisenseLink": "https://creativecommons.org/licenses/by-sa/3.0/deed.zh",
    "name": "C 语言入门教程_阮一峰",
    "order": 270000,
    "parentChapterId": 547,
    "userControlSetTop": false,
    "visible": 1
     */
    val author: String,
    val courseId: Int,
    val cover: String,
    val desc: String,
    val id: Int,
    val lisense: String,
    val lisenseLink: String,
    val name: String,
    val order: Int,
    val parentChapterId: Int,
    val userControlSetTop: Boolean,
    val visible: Int
): Parcelable