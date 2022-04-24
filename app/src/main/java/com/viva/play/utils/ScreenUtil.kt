package com.viva.play.utils

import android.content.res.Resources
import android.os.Build

/**
 * @author 李雄厚
 *
 * @features 屏幕工具类
 */

/**
 * 屏幕宽度
 */
val screenWidth get() = Resources.getSystem().displayMetrics.widthPixels

/**
 * 屏幕高度
 */
val screenHeight get() = Resources.getSystem().displayMetrics.heightPixels

/**
 * 获取状态栏高度
 */
private fun getStatusBarHeight(): Int {
    val barHeight: Int
    val resId = Resources.getSystem()
        .getIdentifier("status_bar_height", "dimen", "android")
    barHeight = if (resId > 0) {
        Resources.getSystem().getDimensionPixelSize(resId)
    } else {
        24F.dpToPx
    }
    return barHeight
}

/**
 * 获取状态栏高度(实际使用)
 */
val realStatusBarHeight: Int
    get() {
        var barHeight = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            barHeight = getStatusBarHeight()
        }
        return barHeight
    }