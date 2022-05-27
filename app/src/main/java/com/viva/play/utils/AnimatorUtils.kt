package com.viva.play.utils

import android.animation.ValueAnimator
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.widget.AppCompatTextView

/**
 * @author 李雄厚
 *
 *
 */
fun AppCompatTextView.doIntAnim(to: Int, duration: Long) {
    val fromStr = this.text.toString().trim()
    val from = try {
        fromStr.toInt()
    } catch (e: Exception) {
        0
    }
    this.doIntAnim(from, to, duration)
}

private fun AppCompatTextView.doIntAnim(from: Int, to: Int, duration: Long) {
    val animator = ValueAnimator.ofInt(from, to)
    animator.addUpdateListener {
        val value = it.animatedValue
        this.text = String.format("%d", value)
    }
    animator.duration = duration
    animator.interpolator = DecelerateInterpolator()
    animator.start()
}