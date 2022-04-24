package com.viva.play.utils

import android.content.res.Resources

/**
 * @author 李雄厚
 *
 * @features 单位转换
 */

private val density get() = Resources.getSystem().displayMetrics.density

/**
 * dp --> px
 */
val Number.dpToPx get() = (this.toFloat() * density + 0.5f).toInt()

/**
 * px --> dp
 */
val Number.pxToDp get() = (this.toFloat() / density + 0.5f).toInt()

/**
 * sp --> px
 */
val Number.spToPx: Int get() {
    val sd = Resources.getSystem().displayMetrics.scaledDensity
     return (this.toFloat() * sd + 0.5f).toInt()
}

/**
 * px --> sp
 */
val Number.pxToSp: Int get() {
    val sd = Resources.getSystem().displayMetrics.scaledDensity
    return (this.toFloat() / sd + 0.5f).toInt()
}
