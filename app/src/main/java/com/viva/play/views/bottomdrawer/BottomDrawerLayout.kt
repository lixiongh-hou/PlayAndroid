package com.viva.play.views.bottomdrawer

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.Scroller
import androidx.core.view.*
import com.viva.play.utils.dpToPx
import java.lang.ref.WeakReference
import kotlin.math.abs

class BottomDrawerLayout : FrameLayout, NestedScrollingParent2 {

    private val _dismissDuration = 300
    private val _dismissVelocity = 1000F
    private val _dismissFraction = 0.1F

    private val mDragHelper: androidx.customview.widget.ViewDragHelper =
        androidx.customview.widget.ViewDragHelper.create(this, DragCallback())
    private val mNestedHelper: NestedScrollingParentHelper = NestedScrollingParentHelper(this)
    private val mScroller: Scroller = Scroller(context, DecelerateInterpolator())

    private var mInnerScrollViews: List<View>? = null
    private var mHandleTouchEvent = false
    private var mDownX: Float = 0.toFloat()
    private var mDownY: Float = 0.toFloat()
    private var mLeft: Int = 0
    private var mRight: Int = 0
    private var mTop: Int = 0
    private var mBottom: Int = 0

    private var usingNested = false

    private var mDragFraction = 0f

    private var onDragStart: (() -> Unit)? = null
    private var onDragging: ((f: Float) -> Unit)? = null
    private var onDragEnd: (() -> Unit)? = null
    private var onOpened: (() -> Unit)? = null
    private var onClosed: (() -> Unit)? = null

    private var enable: Boolean = true
    private var draggable: Boolean = true

    private var moveDownY = 0F
    private var moveUpOrDown: Boolean? = null

    private var open = true
    private var openTopMarginStatusBarHeight = false
    private var closeHeight: Int = 49.dpToPx
    private var openTopMargin: Int = 0

    private val handleViews = arrayListOf<WeakReference<View>>()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        mDragFraction = if (open) 0F else 1F
    }

    fun addHandleView(vararg views: View) {
        views.forEach { handleViews.add(WeakReference(it)) }
    }

    fun removeHandleView(vararg views: View) {
        val removeViews = views.toList()
        val iterator = handleViews.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (next.get() == null || removeViews.contains(next.get())) {
                next.clear()
                iterator.remove()
            }
        }
    }

    fun clearHandleViews() {
        handleViews.forEach { it.clear() }
        handleViews.clear()
    }

    fun cleanHandleViewIfRecycled() {
        val iterator = handleViews.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (next.get() == null) {
                next.clear()
                iterator.remove()
            }
        }
    }

    fun onDragStart(listener: () -> Unit) {
        onDragStart = listener
    }

    fun onDragging(listener: (f: Float) -> Unit) {
        onDragging = listener
    }

    fun onDragEnd(listener: () -> Unit) {
        onDragEnd = listener
    }

    fun onOpened(listener: () -> Unit) {
        onOpened = listener
    }

    fun onClosed(listener: () -> Unit) {
        onClosed = listener
    }

    fun setCloseHeight(height: Int) {
        this.closeHeight = height
        requestLayout()
    }

    fun getCloseHeight(): Int {
        return this.closeHeight
    }

    fun setOpenTopMargin(margin: Int) {
        this.openTopMargin = margin
        requestLayout()
    }

    fun getOpenTopMargin(): Int {
        return this.openTopMargin
    }

    fun setDraggable(draggable: Boolean) {
        this.draggable = draggable
    }

    fun isDraggable(): Boolean {
        return this.draggable
    }

    fun setEnable(enable: Boolean) {
        this.enable = enable
    }

    fun isOpen(): Boolean {
        return open
    }

    fun isOpened(): Boolean {
        return getDragY() == getMinDragY()
    }

    fun isClosed(): Boolean {
        return getDragY() == getMaxDragY()
    }

    fun toggle(duration: Int = 300) {
        if (!enable) return
        if (isOpened()) {
            close(duration)
        } else if (isClosed()) {
            open(duration)
        }
    }

    fun open(duration: Int = 300) {
        if (!enable) return
        open = true
        if (duration > 0) {
            usingNested = true
            mScroller.abortAnimation()
            mScroller.startScroll(
                -getDragX().toInt(), -getDragY().toInt(),
                -getDragX().toInt(), -(getMinDragY() - getDragY()).toInt(), duration
            )
            invalidate()
        } else {
            initStateImmediately(true)
        }
    }

    fun close(duration: Int = 300) {
        if (!enable) return
        open = false
        if (duration > 0) {
            usingNested = true
            mScroller.abortAnimation()
            mScroller.startScroll(
                -getDragX().toInt(), -getDragY().toInt(),
                -getDragX().toInt(), -(getMaxDragY() - getDragY()).toInt(), duration
            )
            invalidate()
        } else {
            initStateImmediately(false)
        }
    }

    private fun initStateImmediately(openOrClose: Boolean) {
        open = openOrClose
        if (open) {
            setDragY(getMinDragY())
        } else {
            setDragY(getMaxDragY())
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        dragView.isClickable = true
        if (mInnerScrollViews.isNullOrEmpty()) {
            mInnerScrollViews = DragCompat.findAllScrollViews(dragView)
        }
        val ws = MeasureSpec.getSize(widthMeasureSpec)
        val hs = MeasureSpec.getSize(heightMeasureSpec)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        dragView.measure(
            MeasureSpec.makeMeasureSpec(ws, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(hs - openTopMargin, MeasureSpec.EXACTLY)
        )
    }

    val dragView: View
        get() {
            check(childCount > 0)
            return getChildAt(childCount - 1)
        }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mLeft = dragView.left
        mRight = dragView.right
        mTop = dragView.top
        mBottom = dragView.bottom
        initStateImmediately(open)
        when {
            mDragFraction <= 0F -> initStateImmediately(true)
            mDragFraction >= 1F -> initStateImmediately(false)
            else -> setDragY(mDragFraction * abs(getMaxDragY() - getMinDragY()) + getMinDragY())
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                moveDownY = ev.rawY
                moveUpOrDown = null
            }
            MotionEvent.ACTION_MOVE -> {
                moveUpOrDown = when {
                    ev.rawY - moveDownY > 0F -> false
                    ev.rawY - moveDownY < 0F -> true
                    else -> null
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                moveDownY = 0F
                moveUpOrDown = null
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!draggable) {
            mHandleTouchEvent = false
            return super.onInterceptTouchEvent(ev)
        }
        if (!enable) {
            mHandleTouchEvent = false
            return super.onInterceptTouchEvent(ev)
        }
        when (ev.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                if (ev.x < dragView.left || ev.x > dragView.right || ev.y < dragView.top || ev.y > dragView.bottom) {
                    return false
                }
                mDragHelper.abort()
                mScroller.abortAnimation()
                mDownX = ev.rawX
                mDownY = ev.rawY
                usingNested = false
            }
        }
        if (usingNested) {
            mHandleTouchEvent = false
            return super.onInterceptTouchEvent(ev)
        }
        val shouldIntercept = mDragHelper.shouldInterceptTouchEvent(ev)
        mHandleTouchEvent = shouldIntercept
        when (ev.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                if (!mHandleTouchEvent) {
                    if (!DragCompat.canViewScrollUp(mInnerScrollViews, mDownX, mDownY, false)) {
                        mHandleTouchEvent = true
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (abs(ev.rawX - mDownX) < ViewConfiguration.getTouchSlop() &&
                    abs(ev.rawY - mDownY) < ViewConfiguration.getTouchSlop()
                ) {
                    judgeDragEnd()
                }
            }
        }
        return shouldIntercept
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (!draggable) {
            return super.onTouchEvent(ev)
        }
        if (!enable) {
            return super.onTouchEvent(ev)
        }
        when (ev.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                if (ev.x < dragView.left || ev.x > dragView.right || ev.y < dragView.top || ev.y > dragView.bottom) {
                    return false
                }
            }
        }
        if (usingNested) {
            return super.onTouchEvent(ev)
        }
        mDragHelper.processTouchEvent(ev)
        when (ev.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (abs(ev.rawX - mDownX) < ViewConfiguration.getTouchSlop() &&
                    abs(ev.rawY - mDownY) < ViewConfiguration.getTouchSlop()
                ) {
                    judgeDragEnd()
                }
            }
        }
        return mHandleTouchEvent
    }

    override fun computeScroll() {
        if (!enable) return
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.currX, mScroller.currY)
            invalidate()
        }
        if (mDragHelper.continueSettling(true)) {
            invalidate()
        }
    }

    private fun getDragX(): Float {
        return dragView.left.toFloat() - mLeft.toFloat()
    }

    private fun getDragY(): Float {
        return dragView.top.toFloat() - mTop.toFloat()
    }

    private fun getMinDragY() = 0F

    private fun getMaxDragY() = (dragView.height - closeHeight).toFloat()

    private fun getMinDragX() = 0F

    private fun getMaxDragX() = (dragView.width - closeHeight).toFloat()

    private fun setDragX(dragX: Float) {
        setDrag(dragX, getDragY())
    }

    private fun setDragY(dragY: Float) {
        setDrag(getDragX(), dragY)
    }

    private fun setDrag(dragX: Float, dragY: Float) {
        val x = dragX.range(getMinDragX(), getMaxDragX()).toInt()
        val y = dragY.range(getMinDragY(), getMaxDragY()).toInt()
        dragView.left = mLeft + x
        dragView.right = mRight + x
        dragView.top = mTop + y
        dragView.bottom = mBottom + y
        onDragChanged()
        invalidate()
    }

    private fun Float.range(min: Float, max: Float): Float {
        return when {
            this < min -> min
            this > max -> max
            else -> this
        }
    }

    override fun scrollBy(x: Int, y: Int) {
        setDrag(getDragX() - x, getDragY() - y)
    }

    override fun scrollTo(x: Int, y: Int) {
        setDrag(-x.toFloat(), -y.toFloat())
    }

    private fun refreshDragFraction() {
        mDragFraction = abs(getDragY()) / abs(getMaxDragY() - getMinDragY())
        if (mDragFraction < 0) {
            mDragFraction = 0f
        } else if (mDragFraction > 1) {
            mDragFraction = 1f
        }
    }

    private fun onDragChanged() {
        refreshDragFraction()
        onDragging()
        if (open) {
            if (mDragFraction == 0F) onDragEnd()
        } else {
            if (mDragFraction == 1F) onDragEnd()
        }
    }

    private var dargging = false
    private var touching = false

    private fun onDragStart() {
        dargging = true
        onDragStart?.invoke()
    }

    private fun onDragging() {
        onDragging?.invoke(mDragFraction)
    }

    private fun onDragEnd() {
        dargging = false
        onDragEnd?.invoke()
        if (this.isOpened()) {
            onOpened?.invoke()
        } else if (isClosed()) {
            onClosed?.invoke()
        }
    }

    private fun judgeDragEnd() {
        refreshDragFraction()
        val openOrClose = mDragFraction < 0.5F
        val duration: Int = _dismissDuration
        if (openOrClose) {
            open(duration)
        } else {
            close(duration)
        }
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        usingNested = if (target is NestedScrollingChild) {
            if (mInnerScrollViews?.contains(child) == true) {
                target.canScrollVertically(-1) || target.canScrollVertically(1)
            } else {
                false
            }
        } else {
            false
        }
        return usingNested
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        nestedScrollStopped = false
        mNestedHelper.onNestedScrollAccepted(child, target, axes, type)
        if (type == ViewCompat.TYPE_TOUCH) {
            mScroller.abortAnimation()
            velocity = 0F
            refreshDragFraction()
            touching = true
            onDragStart()
        }
    }

    private var velocity: Float = 0F

    override fun onNestedFling(
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        return if (nestedScrollStopped) {
            false
        } else {
            super.onNestedFling(target, velocityX, velocityY, consumed)
        }
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        velocity = velocityY
        return if (nestedScrollStopped) {
            false
        } else {
            return super.onNestedPreFling(target, velocityX, velocityY)
        }
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        target as ScrollingView
        val scrollY: Int = -getDragY().toInt()
        consumed[0] = 0
        consumed[1] = if (dy > 0) {
            if (scrollY < 0) {
                if (scrollY + dy > 0) {
                    -scrollY
                } else {
                    dy
                }
            } else {
                0
            }
        } else if (dy < 0) {
            if (scrollY < 0) {
                dy
            } else {
                if (target.canScrollVertically(-1)) {
                    0
                } else {
                    if (type == ViewCompat.TYPE_NON_TOUCH) {
                        if (scrollY + dy > 0) {
                            -scrollY
                        } else {
                            0
                        }
                    } else {
                        dy
                    }
                }
            }
        } else {
            0
        }
        scrollBy(consumed[0], consumed[1])
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int/*, consumed: IntArray*/
    ) {
        val scrollY: Int = -getDragY().toInt()
        val consumedx = 0
        val consumedy = if (scrollY + dyUnconsumed > 0) {
            -scrollY
        } else {
            if (type == ViewCompat.TYPE_NON_TOUCH) {
                0
            } else {
                dyUnconsumed
            }
        }
        scrollBy(consumedx, consumedy)
    }

    private var nestedScrollStopped = false

    override fun onStopNestedScroll(target: View, type: Int) {
        mNestedHelper.onStopNestedScroll(target, type)
        if (type == ViewCompat.TYPE_TOUCH) {
            if (!target.canScrollVertically(-1)) {
                touching = false
                nestedScrollStopped = true
                val openOrClose = if (open) {
                    when {
                        velocity > 0F -> true
                        velocity < 0F -> false
                        else -> if (open) {
                            mDragFraction < _dismissFraction
                        } else {
                            mDragFraction < 1F - _dismissFraction
                        }
                    }
                } else {
                    when {
                        velocity > 0F -> true
                        velocity < 0F -> false
                        else -> if (open) {
                            mDragFraction < _dismissFraction
                        } else {
                            mDragFraction < 1F - _dismissFraction
                        }
                    }
                }
                val f = abs(velocity) / _dismissVelocity
                val duration: Int = if (f <= 1) {
                    _dismissDuration
                } else {
                    val d = (_dismissDuration / f).toInt()
                    if (d < 200) 200 else d
                }
                if (openOrClose) {
                    open(duration)
                } else {
                    close(duration)
                }
            }
        }
    }

    override fun getNestedScrollAxes(): Int {
        return mNestedHelper.nestedScrollAxes
    }

    private inner class DragCallback : androidx.customview.widget.ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            if (!enable) return false
            if (child != dragView) return false
            cleanHandleViewIfRecycled()
            handleViews.forEach {
                if (DragCompat.contains(it.get()!!, mDownX, mDownY)) {
                    return true
                }
            }
            if (usingNested) return false
            if (isOpened()) {
                return !DragCompat.canViewScrollUp(mInnerScrollViews, mDownX, mDownY, false)
            }
            if (isClosed()) {
                return !DragCompat.canViewScrollDown(mInnerScrollViews, mDownX, mDownY, false)
            }
            return true
        }

        override fun onViewCaptured(capturedChild: View, activePointerId: Int) {
            super.onViewCaptured(capturedChild, activePointerId)
            refreshDragFraction()
            touching = true
            onDragStart()
        }

        override fun getViewHorizontalDragRange(child: View): Int {
            return 0
        }

        override fun getViewVerticalDragRange(child: View): Int {
            return height - mTop - closeHeight
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return mLeft
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            val pv = if (top > height - closeHeight) {
                height - closeHeight
            } else {
                if (top < mTop) {
                    mTop
                } else {
                    top
                }
            }
            return pv
        }

        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int
        ) {
            super.onViewPositionChanged(changedView, left, top, dx, dy)
            refreshDragFraction()
            onDragging()
            if (open) {
                if (mDragFraction == 0F) onDragEnd()
            } else {
                if (mDragFraction == 1F) onDragEnd()
            }
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            super.onViewReleased(releasedChild, xvel, yvel)
            touching = false
            val openOrClose = if (open) {
                when {
                    yvel > 0F -> false
                    yvel < 0F -> true
                    else -> if (open) {
                        mDragFraction < _dismissFraction
                    } else {
                        mDragFraction < 1F - _dismissFraction
                    }
                }
            } else {
                when {
                    yvel > 0F -> false
                    yvel < 0F -> true
                    else -> if (open) {
                        mDragFraction < _dismissFraction
                    } else {
                        mDragFraction < 1F - _dismissFraction
                    }
                }
            }
            val l = mLeft
            val t = if (openOrClose) {
                mTop
            } else {
                height - closeHeight
            }
            open = openOrClose
            mDragHelper.settleCapturedViewAt(l, t)
            invalidate()
        }
    }
}
