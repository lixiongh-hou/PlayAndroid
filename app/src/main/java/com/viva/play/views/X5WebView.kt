package com.viva.play.views

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.ScrollingView
import com.tencent.smtt.sdk.WebView
import com.viva.play.views.draggable.IQMUIContinuousNestedScrollCommon
import com.viva.play.views.draggable.IQMUIContinuousNestedTopView

/**
 * @author 李雄厚
 *
 *
 */
class X5WebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : WebView(context, attrs), ScrollingView, IQMUIContinuousNestedTopView {

    override fun canScrollHorizontally(direction: Int): Boolean {
        return view.canScrollHorizontally(direction)
    }

    override fun canScrollVertically(direction: Int): Boolean {
        return view.canScrollVertically(direction)
    }


    private var mScrollNotifier: IQMUIContinuousNestedScrollCommon.OnScrollNotifier? = null

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

    override fun injectScrollNotifier(notifier: IQMUIContinuousNestedScrollCommon.OnScrollNotifier) {
        mScrollNotifier = notifier
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        mScrollNotifier?.notify(getCurrentScroll(), getScrollOffsetRange())
    }
}