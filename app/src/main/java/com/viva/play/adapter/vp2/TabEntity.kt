package com.viva.play.adapter.vp2

/**
 * @author 李雄厚
 *
 *
 */
data class TabEntity(
    val tabName: String,
    val tabIcon: String,
    val tabIconNight: String,
    var isNight: Boolean,
    val msgCount: Int
)