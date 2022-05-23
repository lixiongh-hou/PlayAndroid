package com.viva.play.views.draggable

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView

/**
 * @author 李雄厚
 *
 *
 */
class QMUINestedScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : NestedScrollView(context, attrs), IQMUIContinuousNestedTopView {

    private var mScrollNotifier: IQMUIContinuousNestedScrollCommon.OnScrollNotifier? = null

    override fun consumeScroll(dyUnconsumed: Int): Int {
        // compute the consumed value
        var scrollY = scrollY
        val maxScrollY = getScrollOffsetRange()
        // the scrollY may be negative or larger than scrolling range
        // the scrollY may be negative or larger than scrolling range
        scrollY = 0.coerceAtLeast(scrollY.coerceAtMost(maxScrollY))
        var dy = 0
        if (dyUnconsumed < 0) {
            dy = dyUnconsumed.coerceAtLeast(-scrollY)
        } else if (dyUnconsumed > 0) {
            dy = dyUnconsumed.coerceAtMost(maxScrollY - scrollY)
        }
        scrollBy(0, dy)
        return dyUnconsumed - dy
    }

    @SuppressLint("RestrictedApi")
    override fun getCurrentScroll(): Int {
        return computeVerticalScrollOffset()
    }

    @SuppressLint("RestrictedApi")
    override fun getScrollOffsetRange(): Int {
        return 0.coerceAtLeast(computeVerticalScrollRange() - height)
    }

    override fun injectScrollNotifier(notifier: IQMUIContinuousNestedScrollCommon.OnScrollNotifier) {
        mScrollNotifier = notifier
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        mScrollNotifier?.notify(getCurrentScroll(), getScrollOffsetRange())

    }
}