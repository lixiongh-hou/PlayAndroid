package com.viva.play.service.entity

/**
 * @author 李雄厚
 *
 * 个人积分信息
 */
data class CoinRecordEntity(
    val coinCount: Int,
    val rank: Int,
    val userId: Int,
    val username: String
)