package com.viva.play.service.entity

/**
 * @author 李雄厚
 *
 *
 */
data class CollectLinkEntity(
    val id: Int,
    val link: String,
    val name: String,
    val userId: Int,
    val visible: Int
)