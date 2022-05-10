package com.viva.play.utils

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange

/**
 * @author 李雄厚
 *
 *
 */
object ColorUtils {

    fun changingColor(
        @ColorInt colorFrom: Int,
        @ColorInt colorTo: Int,
        @FloatRange(from = 0.0, to = 1.0) fraction: Float
    ): Int {
        val redStart = Color.red(colorFrom)
        val blueStart = Color.blue(colorFrom)
        val greenStart = Color.green(colorFrom)
        val alphaStart = Color.alpha(colorFrom)

        val redEnd = Color.red(colorTo)
        val blueEnd = Color.blue(colorTo)
        val greenEnd = Color.green(colorTo)
        val alphaEnd = Color.alpha(colorTo)

        val redDifference = redEnd - redStart
        val blueDifference = blueEnd - blueStart
        val greenDifference = greenEnd - greenStart
        val alphaDifference = alphaEnd - alphaStart

        val redCurrent = (redStart + fraction * redDifference).toInt()
        val blueCurrent = (blueStart + fraction * blueDifference).toInt()
        val greenCurrent = (greenStart + fraction * greenDifference).toInt()
        val alphaCurrent = (alphaStart + fraction * alphaDifference).toInt()

        return Color.argb(alphaCurrent, redCurrent, greenCurrent, blueCurrent)
    }
}