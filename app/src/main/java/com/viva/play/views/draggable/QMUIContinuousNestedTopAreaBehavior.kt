package com.viva.play.views.draggable

import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.view.animation.Interpolator
import android.webkit.WebView
import android.widget.OverScroller
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import kotlin.math.abs

/**
 * @author 李雄厚
 *
 *
 */
class QMUIContinuousNestedTopAreaBehavior : QMUIViewOffsetBehavior<View> {
    companion object {
        private const val INVALID_POINTER = -1
        private val QUNITIC_INTERPOLATOR = Interpolator { t ->
            var t1 = t
            t1 -= 1.0f
            t1 * t1 * t1 * t1 * t1 + 1.0f
        }
    }

    private var mViewFlinger: ViewFlinger? = null
    private val mScrollConsumed = IntArray(2)

    private var isBeingDragged = false
    private var activePointerId = INVALID_POINTER
    private var lastMotionY = 0
    private var touchSlop = -1
    private var velocityTracker: VelocityTracker? = null
    private var mCallback: Callback? = null
    private var isInTouch = false
    private var isInFlingOrScroll = false
    private var replaceCancelActionWithMoveActionForWebView = true

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        mViewFlinger = ViewFlinger(context)
    }

    fun setReplaceCancelActionWithMoveActionForWebView(replaceCancelActionWithMoveActionForWebView: Boolean) {
        this.replaceCancelActionWithMoveActionForWebView =
            replaceCancelActionWithMoveActionForWebView
    }

    fun setCallback(callback: Callback) {
        mCallback = callback
    }

    override fun onInterceptTouchEvent(
        parent: CoordinatorLayout,
        child: View,
        ev: MotionEvent
    ): Boolean {
        if (touchSlop < 0) {
            touchSlop = ViewConfiguration.get(parent.context).scaledTouchSlop
        }
        val action = ev.action
        if (action == MotionEvent.ACTION_MOVE && isBeingDragged) {
            return true
        }

        run b@{
            when (ev.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    mViewFlinger?.stop()
                    isInTouch = true
                    isBeingDragged = false
                    val x = ev.x.toInt()
                    val y = ev.y.toInt()
                    if (parent.isPointInChildBounds(child, x, y)) {
                        lastMotionY = y
                        activePointerId = ev.getPointerId(0)
                        ensureVelocityTracker()
                    }
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    val actionIndex = ev.actionIndex
                    return (actionIndex != 0 &&
                            !parent.isPointInChildBounds(child, ev.x.toInt(), ev.y.toInt())
                            && parent.isPointInChildBounds(
                        child, ev.getX(actionIndex).toInt(), ev.getY(actionIndex).toInt()
                    ))
                }
                MotionEvent.ACTION_MOVE -> {
                    val activePointerId = activePointerId
                    if (activePointerId == INVALID_POINTER) {
                        // If we don't have a valid id, the touch down wasn't on content.
                        return@b
                    }
                    val pointerIndex = ev.findPointerIndex(activePointerId)
                    if (pointerIndex == -1) {
                        return@b
                    }
                    val y = ev.getY(pointerIndex).toInt()
                    val yDiff = abs(y - lastMotionY)
                    if (yDiff > touchSlop) {
                        isBeingDragged = true
                        if (child is WebView) {
                            // dispatch cancel event not work in webView sometimes.
                            val cancelEvent = MotionEvent.obtain(ev)
                            cancelEvent.offsetLocation(-child.left.toFloat(), -child.top.toFloat())
                            if (replaceCancelActionWithMoveActionForWebView) {
                                cancelEvent.action = MotionEvent.ACTION_MOVE
                            } else {
                                cancelEvent.action = MotionEvent.ACTION_CANCEL
                            }
                            child.dispatchTouchEvent(cancelEvent)
                            cancelEvent.recycle()
                        }
                        lastMotionY = y
                        mCallback?.onTopBehaviorTouchBegin()
                    }
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    isInTouch = false
                    isBeingDragged = false
                    activePointerId = INVALID_POINTER
                    velocityTracker?.recycle()
                    velocityTracker = null
                }
            }
        }

        velocityTracker?.addMovement(ev)

        return isBeingDragged
    }

    override fun onTouchEvent(parent: CoordinatorLayout, child: View, ev: MotionEvent): Boolean {
        if (touchSlop < 0) {
            touchSlop = ViewConfiguration.get(parent.context).scaledTouchSlop
        }
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mViewFlinger?.stop()
                isInTouch = true
                val x = ev.x.toInt()
                val y = ev.y.toInt()
                if (parent.isPointInChildBounds(child, x, y)) {
                    lastMotionY = y
                    activePointerId = ev.getPointerId(0)
                    ensureVelocityTracker()
                } else {
                    return false
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val activePointerIndex = ev.findPointerIndex(activePointerId)
                if (activePointerIndex == -1) {
                    return false
                }
                val y = ev.getY(activePointerIndex).toInt()
                var dy = lastMotionY - y
                if (!isBeingDragged && abs(dy) > touchSlop) {
                    isBeingDragged = true
                    mCallback?.onTopBehaviorTouchBegin()
                    if (dy > 0) {
                        dy -= touchSlop
                    } else {
                        dy += touchSlop
                    }
                }
                if (isBeingDragged) {
                    lastMotionY = y
                    scroll(parent, child, dy)
                }
            }
            MotionEvent.ACTION_UP -> {
                isInTouch = false
                mCallback?.onTopBehaviorTouchEnd()
                if (velocityTracker != null) {
                    velocityTracker?.addMovement(ev)
                    velocityTracker?.computeCurrentVelocity(1000)
                    val yvel = -(velocityTracker!!.getYVelocity(activePointerId) + 0.5f).toInt()
                    mViewFlinger!!.fling(parent, child, yvel)
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                if (isInTouch) {
                    isInTouch = false
                    mCallback?.onTopBehaviorTouchEnd()

                }
                isBeingDragged = false
                activePointerId = INVALID_POINTER
                velocityTracker?.recycle()
                velocityTracker = null
            }
        }
        velocityTracker?.addMovement(ev)

        return true
    }

    fun scroll(parent: CoordinatorLayout, child: View, dy: Int) {
        mScrollConsumed[0] = 0
        mScrollConsumed[1] = 0
        onNestedPreScroll(parent, child, child, 0, dy, mScrollConsumed, ViewCompat.TYPE_TOUCH)
        var unConsumed = dy - mScrollConsumed[1]
        if (child is IQMUIContinuousNestedTopView) {
            unConsumed = (child as IQMUIContinuousNestedTopView).consumeScroll(unConsumed)
        }
        onNestedScroll(
            parent, child, child, 0, dy - unConsumed,
            0, unConsumed, ViewCompat.TYPE_TOUCH
        )
    }

    fun smoothScrollBy(parent: CoordinatorLayout, child: View, dy: Int, duration: Int) {
        mViewFlinger?.startScroll(parent, child, dy, duration)
    }

    fun stopFlingOrScroll() {
        mViewFlinger?.stop()
    }

    private fun ensureVelocityTracker() {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain()
        }
    }

    override fun onMeasureChild(
        parent: CoordinatorLayout,
        child: View,
        parentWidthMeasureSpec: Int,
        widthUsed: Int,
        parentHeightMeasureSpec: Int,
        heightUsed: Int
    ): Boolean {
        val childLpHeight = child.layoutParams.height
        var availableHeight = View.MeasureSpec.getSize(parentHeightMeasureSpec)
        if (childLpHeight == ViewGroup.LayoutParams.MATCH_PARENT) {
            if (availableHeight == 0) {
                // If the measure spec doesn't specify a size, use the current height
                availableHeight = parent.height
            }
            val heightMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(availableHeight, View.MeasureSpec.AT_MOST)
            parent.onMeasureChild(
                child, parentWidthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed
            )
        } else {
            parent.onMeasureChild(
                child,
                parentWidthMeasureSpec,
                widthUsed,
                View.MeasureSpec.makeMeasureSpec(View.MEASURED_SIZE_MASK, View.MeasureSpec.AT_MOST),
                heightUsed
            )
        }
        return true
    }

    override fun onNestedPreScroll(
        parent: CoordinatorLayout,
        child: View,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        if (target.parent !== parent) {
            return
        }
        if (target === child) {
            // both target view and child view is top view
            if (dy < 0) {
                if (child.top <= dy) {
                    setTopAndBottomOffset(child.top - dy - getLayoutTop())
                    consumed[1] += dy
                } else if (child.top < 0) {
                    val top = child.top
                    setTopAndBottomOffset(0 - getLayoutTop())
                    consumed[1] += top
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onNestedScroll(
        parent: CoordinatorLayout, child: View,
        target: View, dxConsumed: Int, dyConsumed: Int,
        dxUnconsumed: Int, dyUnconsumed: Int, type: Int
    ) {
        var dyUnconsumed1 = dyUnconsumed
        if (target.parent !== parent) {
            return
        }
        if (target === child) {
            // both target view and child view is top view
            if (dyUnconsumed1 > 0) {
                val parentBottom = parent.height
                if (target.bottom - parentBottom >= dyUnconsumed1) {
                    setTopAndBottomOffset(target.top - dyUnconsumed1 - getLayoutTop())
                } else if (target.bottom - parentBottom > 0) {
                    val moveDistance = target.bottom - parentBottom
                    setTopAndBottomOffset(target.top - moveDistance - getLayoutTop())
                }
            }
        } else {
            // child is topView, target is bottomView
            if (dyUnconsumed1 < 0) {
                if (child.top <= dyUnconsumed1) {
                    setTopAndBottomOffset(child.top - dyUnconsumed1 - getLayoutTop())
                    return
                } else if (child.top < 0) {
                    val top = child.top
                    setTopAndBottomOffset(0 - getLayoutTop())
                    dyUnconsumed1 =
                        if (dyUnconsumed1 == Int.MIN_VALUE) dyConsumed else dyUnconsumed1 - top
                }
                if (child is IQMUIContinuousNestedTopView) {
                    (child as IQMUIContinuousNestedTopView).consumeScroll(dyUnconsumed1)
                }
            }
        }
    }
    override fun setTopAndBottomOffset(offset: Int): Boolean {
        val ret = super.setTopAndBottomOffset(offset)
        mCallback?.onTopAreaOffset(offset)
        return ret
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View, directTargetChild: View,
        target: View, axes: Int, type: Int
    ): Boolean {
        return axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    inner class ViewFlinger(context: Context) : Runnable {

        private var mLastFlingY = 0
        private var mOverScroller: OverScroller
        var mInterpolator: Interpolator = QUNITIC_INTERPOLATOR

        // When set to true, postOnAnimation callbacks are delayed until the run method completes
        private var mEatRunOnAnimationRequest = false

        // Tracks if postAnimationCallback should be re-attached when it is done
        private var mReSchedulePostAnimationCallback = false

        private var mCurrentParent: CoordinatorLayout? = null
        private var mCurrentChild: View? = null

        init {
            mOverScroller = OverScroller(context, QUNITIC_INTERPOLATOR)
        }

        override fun run() {
            mReSchedulePostAnimationCallback = false
            mEatRunOnAnimationRequest = true

            // Keep a local reference so that if it is changed during onAnimation method, it won't
            // cause unexpected behaviors
            val scroller = mOverScroller
            if (scroller.computeScrollOffset()) {
                val y = scroller.currY
                val unconsumedY = y - mLastFlingY
                mLastFlingY = y
                if (mCurrentParent != null && mCurrentChild != null) {
                    var canScroll = true
                    if (mCurrentParent is QMUIContinuousNestedScrollLayout) {
                        val layout = mCurrentParent as QMUIContinuousNestedScrollLayout
                        if (unconsumedY > 0 && layout.getCurrentScroll() >= layout.getScrollRange()) {
                            canScroll = false
                        } else if (unconsumedY < 0 && layout.getCurrentScroll() <= 0) {
                            canScroll = false
                        }
                    }
                    if (canScroll) {
                        scroll(mCurrentParent!!, mCurrentChild!!, unconsumedY)
                        postOnAnimation()
                    } else {
                        mOverScroller.abortAnimation()
                    }
                }
            }
            mEatRunOnAnimationRequest = false
            if (mReSchedulePostAnimationCallback) {
                internalPostOnAnimation()
            } else {
                mCurrentParent = null
                mCurrentChild = null
                onFlingOrScrollEnd()
            }
        }

        private fun postOnAnimation() {
            if (mEatRunOnAnimationRequest) {
                mReSchedulePostAnimationCallback = true
            } else {
                internalPostOnAnimation()
            }
        }

        private fun internalPostOnAnimation() {
            if (mCurrentChild != null) {
                mCurrentParent?.removeCallbacks(this)
                ViewCompat.postOnAnimation(mCurrentChild!!, this)
            }
        }

        fun fling(parent: CoordinatorLayout, child: View, velocityY: Int) {
            onFlingOrScrollStart(parent, child)
            mOverScroller.fling(
                0,
                0,
                0,
                velocityY,
                Int.MIN_VALUE,
                Int.MAX_VALUE,
                Int.MIN_VALUE,
                Int.MAX_VALUE
            )
            postOnAnimation()
        }

        fun startScroll(parent: CoordinatorLayout, child: View, dy: Int, duration: Int) {
            onFlingOrScrollStart(parent, child)
            mOverScroller.startScroll(0, 0, 0, dy, duration)
            postOnAnimation()
        }

        private fun onFlingOrScrollStart(parent: CoordinatorLayout, child: View) {
            isInFlingOrScroll = true
            mCallback?.onTopBehaviorFlingOrScrollStart()
            mCurrentParent = parent
            mCurrentChild = child
            mLastFlingY = 0
            // Because you can't define a custom interpolator for flinging, we should make sure we
            // reset ourselves back to the teh default interpolator in case a different call
            // changed our interpolator.
            if (mInterpolator !== QUNITIC_INTERPOLATOR) {
                mInterpolator = QUNITIC_INTERPOLATOR
                mOverScroller = OverScroller(mCurrentParent!!.context, QUNITIC_INTERPOLATOR)
            }
        }

        fun stop() {
            mCurrentChild?.removeCallbacks(this)
            mOverScroller.abortAnimation()
            mCurrentChild = null
            mCurrentParent = null
            onFlingOrScrollEnd()
        }

        private fun onFlingOrScrollEnd() {
            if (mCallback != null && isInFlingOrScroll) {
                mCallback?.onTopBehaviorFlingOrScrollEnd()
            }
            isInFlingOrScroll = false
        }
    }

    interface Callback {
        fun onTopAreaOffset(offset: Int)

        fun onTopBehaviorTouchBegin()

        fun onTopBehaviorTouchEnd()

        fun onTopBehaviorFlingOrScrollStart()

        fun onTopBehaviorFlingOrScrollEnd()
    }
}