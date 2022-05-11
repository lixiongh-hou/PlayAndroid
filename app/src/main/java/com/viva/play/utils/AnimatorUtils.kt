package com.viva.play.utils

import android.animation.ValueAnimator
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.widget.AppCompatTextView

/**
 * @author 李雄厚
 *
 *
 */
object AnimatorUtils {

    fun doIntAnim(target: AppCompatTextView, to: Int, duration: Long) {
        val fromStr = target.text.toString().trim()
        val from = try {
            fromStr.toInt()
        } catch (e: Exception) {
            0
        }
        doIntAnim(target, from, to, duration)
    }

    private fun doIntAnim(target: AppCompatTextView, from: Int, to: Int, duration: Long) {
        val animator = ValueAnimator.ofInt(from, to)
        animator.addUpdateListener {
            val value = it.animatedValue
            target.text = String.format("%d", value)
        }
        animator.duration = duration
        animator.interpolator = DecelerateInterpolator()
        animator.start()
    }
}