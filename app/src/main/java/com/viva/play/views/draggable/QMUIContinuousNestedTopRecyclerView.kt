package com.viva.play.views.draggable

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.viva.play.views.draggable.IQMUIContinuousNestedScrollCommon.OnScrollNotifier

/**
 * @author 李雄厚
 *
 *
 */
class QMUIContinuousNestedTopRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RecyclerView(context, attrs), IQMUIContinuousNestedTopView {

    private var mScrollNotifier: OnScrollNotifier? = null
    private val mScrollConsumed = IntArray(2)

    init {
        isVerticalScrollBarEnabled = false
        addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (mScrollNotifier != null) {
                    when (newState) {
                        SCROLL_STATE_IDLE -> {
                            mScrollNotifier?.onScrollStateChange(
                                recyclerView,
                                IQMUIContinuousNestedScrollCommon.SCROLL_STATE_IDLE
                            )
                        }
                        SCROLL_STATE_SETTLING -> {
                            mScrollNotifier?.onScrollStateChange(
                                recyclerView,
                                IQMUIContinuousNestedScrollCommon.SCROLL_STATE_SETTLING
                            )
                        }
                        SCROLL_STATE_DRAGGING -> {
                            mScrollNotifier?.onScrollStateChange(
                                recyclerView,
                                IQMUIContinuousNestedScrollCommon.SCROLL_STATE_DRAGGING
                            )
                        }
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                mScrollNotifier?.notify(
                    recyclerView.computeVerticalScrollOffset(),
                    0.coerceAtLeast(recyclerView.computeVerticalScrollRange() - recyclerView.height)
                )
            }
        })
    }

    override fun consumeScroll(dyUnconsumed: Int): Int {
        var dyUnconsumed1 = dyUnconsumed
        if (dyUnconsumed1 == Int.MIN_VALUE) {
            scrollToPosition(0)
            return Int.MIN_VALUE
        } else if (dyUnconsumed1 == Int.MAX_VALUE) {
            val adapter = adapter
            if (adapter != null) {
                scrollToPosition(adapter.itemCount - 1)
            }
            return Int.MAX_VALUE
        }

        var reStartNestedScroll = false
        if (!hasNestedScrollingParent(ViewCompat.TYPE_TOUCH)) {
            // the scrollBy use ViewCompat.TYPE_TOUCH to handle nested scroll...
            reStartNestedScroll = true
            startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH)

            // and scrollBy only call dispatchNestedScroll, not call dispatchNestedPreScroll
            mScrollConsumed[0] = 0
            mScrollConsumed[1] = 0
            dispatchNestedPreScroll(0, dyUnconsumed1, mScrollConsumed, null, ViewCompat.TYPE_TOUCH)
            dyUnconsumed1 -= mScrollConsumed[1]
        }
        scrollBy(0, dyUnconsumed1)
        if (reStartNestedScroll) {
            stopNestedScroll(ViewCompat.TYPE_TOUCH)
        }
        return 0
    }

    override fun getCurrentScroll(): Int {
        return computeVerticalScrollOffset()
    }

    override fun getScrollOffsetRange(): Int {
        return 0.coerceAtLeast(computeVerticalScrollRange() - height)
    }

    override fun injectScrollNotifier(notifier: OnScrollNotifier) {
        mScrollNotifier = notifier
    }

    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)
        mScrollNotifier?.notify(getCurrentScroll(), getScrollOffsetRange())

    }
}