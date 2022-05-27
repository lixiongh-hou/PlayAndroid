package com.viva.play.views.draggable

import android.content.Context
import android.util.AttributeSet
import com.tencent.smtt.sdk.WebView
import com.viva.play.views.draggable.IQMUIContinuousNestedScrollCommon.OnScrollNotifier

/**
 * @author 李雄厚
 *
 *
 */
class QMUIContinuousNestedTopWebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : WebView(context, attrs), IQMUIContinuousNestedTopView {

    private var mScrollNotifier: OnScrollNotifier? = null

    init {
        isVerticalScrollBarEnabled = false
    }

    override fun consumeScroll(dyUnconsumed: Int): Int {
        // compute the consumed value
        var scrollY = scrollY
        val maxScrollY = getScrollOffsetRange()
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

    override fun getCurrentScroll(): Int {
        val scrollY = scrollY
        val scrollRange = getScrollOffsetRange()
        return 0.coerceAtLeast(scrollY.coerceAtMost(scrollRange))
    }

    override fun getScrollOffsetRange(): Int {
        return computeVerticalScrollRange() - height
    }

    override fun injectScrollNotifier(notifier: OnScrollNotifier) {
        mScrollNotifier = notifier
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        mScrollNotifier?.notify(getCurrentScroll(), getScrollOffsetRange())
    }
}