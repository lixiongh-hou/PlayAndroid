package com.viva.play.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.viva.play.utils.realStatusBarHeight

/**
 * @author 李雄厚
 *
 *
 */
class StatusBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var mBarHeight = realStatusBarHeight
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(mBarHeight, MeasureSpec.EXACTLY)
        )
    }
}