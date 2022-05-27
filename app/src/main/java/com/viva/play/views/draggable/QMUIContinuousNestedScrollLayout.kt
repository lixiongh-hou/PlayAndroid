package com.viva.play.views.draggable

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.isVisible
import com.viva.play.R
import com.viva.play.utils.getThemeColor
import per.goweii.heartview.HeartView
import java.util.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * @author 李雄厚
 *
 *
 */
open class QMUIContinuousNestedScrollLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : CoordinatorLayout(context, attrs),
    QMUIContinuousNestedTopAreaBehavior.Callback, QMUIDraggableScrollBar.Callback {

    private var mTopView: IQMUIContinuousNestedTopView? = null
    private var mTopAreaBehavior: QMUIContinuousNestedTopAreaBehavior? = null
    private var mOnScrollListeners = mutableListOf<OnScrollListener>()
    private val mCheckLayoutAction = Runnable {
        checkLayout()
    }
    private var mKeepBottomAreaStableWhenCheckLayout = false
    private var mDraggableScrollBar: QMUIDraggableScrollBar? = null
    private var mEnableScrollBarFadeInOut = true
    private var mIsDraggableScrollBarEnabled = false
    private var mCurrentScrollState = IQMUIContinuousNestedScrollCommon.SCROLL_STATE_IDLE
    private var mIsDismissDownEvent = false
    private var mDismissDownY = 0.0F
    private var mTouchSlap = -1


    /*-----*/

    private var mTouchSlop = 0F
    private var mTapTimeout = 0L
    private var mDoubleTapTimeout = 0L

    private var mDownTime = 0L
    private var mDownX = 0F
    private var mDownY = 0F
    private var mLastDownX = 0F
    private var mLastDownY = 0F
    private var mLastTouchTime = 0L

    private var doubleClick: ((x: Float, y: Float) -> Unit)? = null
    private var touchDown: (() -> Unit)? = null

    private val mHeartAnimators = LinkedList<Animator>()

    private var doubleClicked = false
    private val doubleClickTimeoutRunnable = Runnable {
        doubleClicked = false
    }
    /*-----*/

    /*-----*/
    init {
        val viewConfiguration = ViewConfiguration.get(context)
        mTouchSlop = viewConfiguration.scaledTouchSlop.toFloat()
        mTapTimeout = ViewConfiguration.getTapTimeout().toLong()
        mDoubleTapTimeout = ViewConfiguration.getDoubleTapTimeout().toLong()
    }
    /*-----*/


    /*-----*/
    fun setOnDoubleClickListener(doubleClick: (x: Float, y: Float) -> Unit) {
        this.doubleClick = doubleClick
    }

    fun setOnTouchDownListener(touchDown: () -> Unit) {
        this.touchDown = touchDown
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mDownX = width / 2f
        mDownY = height / 2f
    }
    /*-----*/

    private fun ensureScrollBar() {
        if (mDraggableScrollBar == null) {
            mDraggableScrollBar = createScrollBar(context)
            mDraggableScrollBar?.setEnableFadeInAndOut(mEnableScrollBarFadeInOut)
            mDraggableScrollBar?.setCallback(this)
            val lp = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            lp.gravity = Gravity.END
            addView(mDraggableScrollBar, lp)
        }
    }

    /**
     * 设置可拖动滚动条启用
     */
    fun setDraggableScrollBarEnabled(draggableScrollBarEnabled: Boolean) {
        if (mIsDraggableScrollBarEnabled != draggableScrollBarEnabled) {
            mIsDraggableScrollBarEnabled = draggableScrollBarEnabled
            if (mIsDraggableScrollBarEnabled && !mEnableScrollBarFadeInOut) {
                ensureScrollBar()
                mDraggableScrollBar?.setPercent(getCurrentScrollPercent())
                mDraggableScrollBar?.awakenScrollBar()
            }
            mDraggableScrollBar?.isVisible = draggableScrollBarEnabled
        }
    }

    /**
     * 设置淡入淡出
     */
    fun setEnableScrollBarFadeInOut(enableScrollBarFadeInOut: Boolean) {
        if (mEnableScrollBarFadeInOut != enableScrollBarFadeInOut) {
            mEnableScrollBarFadeInOut = enableScrollBarFadeInOut
            if (mIsDraggableScrollBarEnabled && !mEnableScrollBarFadeInOut) {
                ensureScrollBar()
                mDraggableScrollBar?.setPercent(getCurrentScrollPercent())
                mDraggableScrollBar?.awakenScrollBar()
            }
            mDraggableScrollBar?.setEnableFadeInAndOut(enableScrollBarFadeInOut)
            mDraggableScrollBar?.invalidate()
        }
    }

    open fun createScrollBar(context: Context): QMUIDraggableScrollBar =
        QMUIDraggableScrollBar(context)

    override fun onDragStarted() {
        stopScroll()
    }

    override fun onDragToPercent(percent: Float) {
        val targetScroll = (getScrollRange() * percent).toInt()
        scrollBy(targetScroll - getCurrentScroll())
    }

    override fun onDragEnd() {
    }

    fun getCurrentScroll(): Int {
        var currentScroll = 0
        if (mTopView != null) {
            currentScroll += mTopView!!.getCurrentScroll()
        }
        currentScroll += getOffsetCurrent()
        return currentScroll
    }

    fun getScrollRange(): Int {
        var totalRange = 0
        if (mTopView != null) {
            totalRange += mTopView!!.getScrollOffsetRange()
        }
        totalRange += getOffsetRange()
        return totalRange
    }

    private fun getCurrentScrollPercent(): Float {
        val scrollRange = getScrollRange()
        return if (scrollRange == 0) {
            0.0F
        } else getCurrentScroll() * 1F / scrollRange
    }

    fun addOnScrollListener(onScrollListener: OnScrollListener) {
        if (!mOnScrollListeners.contains(onScrollListener)) {
            mOnScrollListeners.add(onScrollListener)
        }
    }

    fun removeOnScrollListener(onScrollListener: OnScrollListener) {
        mOnScrollListeners.remove(onScrollListener)
    }

    fun setKeepBottomAreaStableWhenCheckLayout(keepBottomAreaStableWhenCheckLayout: Boolean) {
        mKeepBottomAreaStableWhenCheckLayout = keepBottomAreaStableWhenCheckLayout
    }

    fun isKeepBottomAreaStableWhenCheckLayout(): Boolean = mKeepBottomAreaStableWhenCheckLayout

    fun setTopAreaView(topView: View, layoutParams: LayoutParams?) {
        check(topView is IQMUIContinuousNestedTopView) { "topView must implement from IQMUIContinuousNestedTopView" }
        if (mTopView != null) {
            removeView(mTopView as View)
        }
        mTopView = topView
        mTopView?.injectScrollNotifier(object : IQMUIContinuousNestedScrollCommon.OnScrollNotifier {
            override fun notify(innerOffset: Int, innerRange: Int) {
                val offsetCurrent =
                    if (mTopAreaBehavior == null) 0 else -mTopAreaBehavior!!.getTopAndBottomOffset()
                val bottomCurrent = 0
                val bottomRange = 0
                dispatchScroll(
                    innerOffset,
                    innerRange,
                    offsetCurrent,
                    getOffsetRange(),
                    bottomCurrent,
                    bottomRange
                )
            }

            override fun onScrollStateChange(view: View, newScrollState: Int) {
            }
        })
        var layoutParams1 = layoutParams
        if (layoutParams1 == null) {
            layoutParams1 = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        val behavior = layoutParams1.behavior
        if (behavior is QMUIContinuousNestedTopAreaBehavior) {
            mTopAreaBehavior = behavior
        } else {
            mTopAreaBehavior = QMUIContinuousNestedTopAreaBehavior(context)
            layoutParams1.behavior = mTopAreaBehavior
        }
        mTopAreaBehavior?.setCallback(this)
        topView.layoutParams = layoutParams1
//        addView(topView, 0, layoutParams1)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        postCheckLayout()
    }

    private fun postCheckLayout() {
        removeCallbacks(mCheckLayoutAction)
        post(mCheckLayoutAction)
    }

    open fun checkLayout() {
        if (mTopView == null) {
            return
        }
        val topCurrent = mTopView!!.getCurrentScroll()
        val topRange = mTopView!!.getScrollOffsetRange()
        val offsetCurrent = -mTopAreaBehavior!!.getTopAndBottomOffset()
        val offsetRange: Int = getOffsetRange()
        if (offsetRange <= 0) {
            return
        }
        if (offsetCurrent >= offsetRange || offsetCurrent > 0 && mKeepBottomAreaStableWhenCheckLayout) {
            mTopView!!.consumeScroll(Int.MAX_VALUE)
            return
        }
        if (topCurrent < topRange && offsetCurrent > 0) {
            val remain = topRange - topCurrent
            if (offsetCurrent >= remain) {
                mTopView!!.consumeScroll(Int.MAX_VALUE)
                mTopAreaBehavior!!.setTopAndBottomOffset(remain - offsetCurrent)
            } else {
                mTopView!!.consumeScroll(offsetCurrent)
                mTopAreaBehavior!!.setTopAndBottomOffset(0)
            }
        }
    }

    private fun dispatchScroll(
        topCurrent: Int, topRange: Int,
        offsetCurrent: Int, offsetRange: Int,
        bottomCurrent: Int, bottomRange: Int
    ) {
        if (mIsDraggableScrollBarEnabled) {
            ensureScrollBar()
            mDraggableScrollBar?.setPercent(getCurrentScrollPercent())
            mDraggableScrollBar?.awakenScrollBar()
        }
        for (onScrollListener in mOnScrollListeners) {
            onScrollListener.onScroll(
                this, topCurrent, topRange, offsetCurrent, offsetRange,
                bottomCurrent, bottomRange
            )
        }
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)
        if (dyUnconsumed > 0 && getCurrentScroll() >= getScrollRange()) {
            // RecyclerView does not stop scroller when over scroll with NestedScrollingParent
            stopScroll()
        }
    }

    private fun dispatchScrollStateChange(newScrollState: Int, fromTopBehavior: Boolean) {
        for (onScrollListener in mOnScrollListeners) {
            onScrollListener.onScrollStateChange(this, newScrollState, fromTopBehavior)
        }
        mCurrentScrollState = newScrollState
    }

    private fun scrollBy(dy: Int) {
        mTopAreaBehavior?.scroll(this, (mTopView as View?)!!, dy)
    }

    private fun stopScroll() {
        mTopAreaBehavior?.stopFlingOrScroll()
    }

    /**
     * 平滑滚动
     */
    fun smoothScrollBy(dy: Int, duration: Int) {
        if (dy == 0) {
            return
        }
        mTopAreaBehavior?.smoothScrollBy(this, (mTopView as View?)!!, dy, duration)
    }

    /**
     * 滚动到顶部
     */
    fun scrollToTop() {
        if (mTopView != null) {
            mTopAreaBehavior?.setTopAndBottomOffset(0)
            mTopView?.consumeScroll(Int.MIN_VALUE)
        }
    }

    /**
     * 滚动到底部
     */
    fun scrollToBottom() {
        mTopView?.consumeScroll(Int.MAX_VALUE)
    }

    private fun getOffsetCurrent(): Int {
        return if (mTopAreaBehavior == null) 0 else -mTopAreaBehavior!!.getTopAndBottomOffset()
    }

    private fun getOffsetRange(): Int {
        if (mTopView == null) {
            return 0
        }
        if (mTopView == null) {
            return 0
        }
        return 0.coerceAtLeast((mTopView as View).height + 0 - height)
    }

    override fun onTopAreaOffset(offset: Int) {
        val topCurrent = if (mTopView == null) 0 else mTopView!!.getCurrentScroll()
        val topRange = if (mTopView == null) 0 else mTopView!!.getScrollOffsetRange()
        val bottomCurrent = 0
        val bottomRange = 0
        dispatchScroll(topCurrent, topRange, -offset, getOffsetRange(), bottomCurrent, bottomRange)
    }

    override fun onTopBehaviorTouchBegin() {
        dispatchScrollStateChange(
            IQMUIContinuousNestedScrollCommon.SCROLL_STATE_DRAGGING, true
        )
    }

    override fun onTopBehaviorTouchEnd() {
        dispatchScrollStateChange(
            IQMUIContinuousNestedScrollCommon.SCROLL_STATE_IDLE, true
        )
    }

    override fun onTopBehaviorFlingOrScrollStart() {
        dispatchScrollStateChange(
            IQMUIContinuousNestedScrollCommon.SCROLL_STATE_SETTLING, true
        )
    }

    override fun onTopBehaviorFlingOrScrollEnd() {
        dispatchScrollStateChange(
            IQMUIContinuousNestedScrollCommon.SCROLL_STATE_IDLE, true
        )
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {

            /*---*/
            mDownX = ev.x
            mDownY = ev.y
            mDownTime = System.currentTimeMillis()
            onTouchDown()
            /*---*/

            if (mCurrentScrollState != IQMUIContinuousNestedScrollCommon.SCROLL_STATE_IDLE) {
                // must stop scroll and not use the current down event.
                // this is worked when topView scroll to bottomView or bottomView scroll to topView.
                stopScroll()
                mIsDismissDownEvent = true
                mDismissDownY = ev.y
                if (mTouchSlap < 0) {
                    mTouchSlap = ViewConfiguration.get(context).scaledTouchSlop
                }
                return true
            }
        } else if (ev.action == MotionEvent.ACTION_MOVE && mIsDismissDownEvent) {
            if (abs(ev.y - mDismissDownY) > mTouchSlap) {
                val down = MotionEvent.obtain(ev)
                down.action = MotionEvent.ACTION_DOWN
                down.offsetLocation(0f, mDismissDownY - ev.y)
                super.dispatchTouchEvent(down)
                down.recycle()
            } else {
                return true
            }
        }else if (ev.action == MotionEvent.ACTION_UP){
            /*---*/
            val upX = ev.x
            val upY = ev.y
            val currTime = System.currentTimeMillis()
            val inTouchSlop = getDistance(
                mDownX.toDouble(),
                mDownY.toDouble(),
                upX.toDouble(),
                upY.toDouble()
            ) < mTouchSlop
            val inTapTimeout = currTime - mDownTime < mTapTimeout
            val isClick = inTouchSlop && inTapTimeout
            if (isClick) {
                if (currTime - mLastTouchTime < mDoubleTapTimeout) {
                    if (getDistance(
                            mDownX.toDouble(),
                            mDownY.toDouble(),
                            mLastDownX.toDouble(),
                            mLastDownY.toDouble()
                        ) < mTouchSlop * 5
                    ) {
                        if (!doubleClicked) {
                            doubleClicked = true
                            onDoubleClicked(upX, upY)
                        }
                    }
                }
                mLastDownX = mDownX
                mLastDownY = mDownY
                mLastTouchTime = currTime
            }
            if (doubleClicked && inTouchSlop) {
                onDoubleClicking(upX, upY)
            }
            removeCallbacks(doubleClickTimeoutRunnable)
            postDelayed(doubleClickTimeoutRunnable, mDoubleTapTimeout * 3)
            /*---*/
        }
        mIsDismissDownEvent = false
        return super.dispatchTouchEvent(ev)
    }

    /*---*/
    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
    }

    override fun onDetachedFromWindow() {
        for (animator in mHeartAnimators) {
            animator.cancel()
        }
        mHeartAnimators.clear()
        super.onDetachedFromWindow()
    }

    private fun getDistance(x1: Double, y1: Double, x2: Double, y2: Double): Double {
        return sqrt(abs(x2 - x1).pow(2.0) + abs(y2 - y1).pow(2.0))
    }

    private fun onDoubleClicked(x: Float, y: Float) {
        doubleClick?.invoke(x, y)
    }

    private fun onDoubleClicking(x: Float, y: Float) {
        showHeartAnim(x, y)
    }

    private fun onTouchDown() {
        touchDown?.invoke()
    }

    private fun showHeartAnim(x: Float, y: Float) {
        val heartView = createHeartView()
        heartView.visibility = INVISIBLE
        heartView.post {
            heartView.pivotX = heartView.width / 2F
            heartView.pivotY = heartView.height.toFloat()
            heartView.x = x - heartView.width / 2F
            heartView.y = y - heartView.height
            val animator = createHeartAnim(heartView)
            mHeartAnimators.add(animator)
            animator.start()
        }
        addView(heartView)
    }

    private val mRandom = Random()
    private fun createHeartAnim(heartView: View): Animator {
        val random = mRandom.nextFloat()
        val rotation = when {
            random < 0.3F -> 15F
            random > 0.7F -> -15F
            else -> 0F
        }
        heartView.rotation = rotation
        val alphaIn = ObjectAnimator.ofFloat(heartView, "alpha", 0F, 1F)
        alphaIn.interpolator = DecelerateInterpolator()
        val scaleXIn = ObjectAnimator.ofFloat(heartView, "scaleX", 0.3F, 1F)
        scaleXIn.interpolator = OvershootInterpolator()
        val scaleYIn = ObjectAnimator.ofFloat(heartView, "scaleY", 0.3F, 1F)
        scaleYIn.interpolator = OvershootInterpolator()
        val setIn = AnimatorSet()
        setIn.playTogether(alphaIn, scaleXIn, scaleYIn)
        setIn.duration = 300
        val alphaOut = ObjectAnimator.ofFloat(heartView, "alpha", 1F, 0F)
        alphaOut.interpolator = AccelerateInterpolator()
        val scaleXOut = ObjectAnimator.ofFloat(heartView, "scaleX", 1F, 1.5F)
        scaleXOut.interpolator = AccelerateInterpolator()
        val scaleYOut = ObjectAnimator.ofFloat(heartView, "scaleY", 1F, 1.5F)
        scaleYOut.interpolator = AccelerateInterpolator()
        val transYOut = ObjectAnimator.ofFloat(
            heartView, "translationY",
            heartView.translationY, heartView.translationY - heartView.height * 1.5F
        )
        transYOut.interpolator = AccelerateInterpolator()
        val setOut = AnimatorSet()
        setOut.playTogether(alphaOut, scaleXOut, scaleYOut, transYOut)
        setOut.duration = 500
        setOut.startDelay = 200
        val set = AnimatorSet()
        set.playSequentially(setIn, setOut)
        set.doOnStart {
            heartView.isVisible = true
        }
        set.doOnEnd {
            removeView(heartView)
        }
        return set
    }

    private fun createHeartView(): View {
        val heartView = HeartView(context)
        heartView.setCenter(-0.5F, -0.5F)
        heartView.setRadiusPercent(0.6F)
        heartView.setColor(context.getThemeColor(R.attr.colorHeartCenter))
        heartView.setEdgeColor(context.getThemeColor(R.attr.colorHeartCenter))
        heartView.setStrokeWidthDp(0F)
        val size = (width * 0.27F).toInt()
        heartView.layoutParams = FrameLayout.LayoutParams(size, size)
        return heartView
    }
    /*---*/

    interface OnScrollListener {
        fun onScroll(
            scrollLayout: QMUIContinuousNestedScrollLayout, topCurrent: Int, topRange: Int,
            offsetCurrent: Int, offsetRange: Int,
            bottomCurrent: Int, bottomRange: Int
        )

        fun onScrollStateChange(
            scrollLayout: QMUIContinuousNestedScrollLayout,
            newScrollState: Int,
            fromTopBehavior: Boolean
        )
    }


}