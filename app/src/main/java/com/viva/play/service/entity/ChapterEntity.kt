package com.viva.play.service.entity

/**
 * @author 李雄厚
 *
 *
 */
data class ChapterEntity(
    val author: String,
    val children: List<Children>,
    val courseId: Int,
    val cover: String,
    val desc: String,
    val id: Int,
    val name: String,
    val order: Int,
    val parentChapterId: Int,
    val userControlSetTop: Boolean,
    val visible: Int
)

data class Children(
    val author: String,
    val courseId: Int,
    val cover: String,
    val desc: String,
    val id: Int,
    val name: String,
    val order: Int,
    val parentChapterId: Int,
    val userControlSetTop: Boolean,
    val visible: Int
)