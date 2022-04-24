package com.viva.play.utils

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.fondesa.recyclerviewdivider.dividerBuilder
import com.viva.play.R

/**
 * 解决嵌套问题
 */
fun RecyclerView.solveNestQuestion() {
    // 解决数据加载不全的问题
    isNestedScrollingEnabled = false
    setHasFixedSize(true)
    // 解决数据加载完成后，没有停留在顶部的问题
    isFocusable = false
}

/**
 * 清除动画，防止刷新时候闪烁
 */
fun RecyclerView.cancelAnimation() {
    itemAnimator?.addDuration = 0
    itemAnimator?.changeDuration = 0
    itemAnimator?.moveDuration = 0
    itemAnimator?.removeDuration = 0
    (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    itemAnimator = null
}

fun RecyclerView.bindDivider() {
    context.dividerBuilder()
        .color(context.getThemeColor(R.attr.colorLine))
        .size(2)
        .build()
        .addTo(this)
}