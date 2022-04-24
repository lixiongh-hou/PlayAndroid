package com.viva.play.service.entity

/**
 * @author 李雄厚
 *
 */
data class UserInfo(
    val admin: Boolean,
    val coinCount: Int,
    val collectIds: List<Int>,
    val email: String,
    val icon: String,
    val id: Int,
    val nickname: String,
    val password: String,
    val publicName: String,
    val token: String,
    val type: Int,
    val username: String
)