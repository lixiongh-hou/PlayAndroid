package com.viva.play.utils

import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.annotation.AttrRes

/**
 * @author 李雄厚
 *
 *
 */


fun View.getThemeColor(@AttrRes id: Int): Int {
    return context.getThemeColor(id)
}

fun Context.getThemeColor(@AttrRes id: Int): Int {
    val typedArray = obtainStyledAttributes(intArrayOf(id))
    val color = typedArray.getColor(0, Color.TRANSPARENT)
    typedArray.recycle()
    return color
}

fun Context.bindColor(@AttrRes id: Int) = lazy(LazyThreadSafetyMode.NONE) {
    getThemeColor(id)
}
