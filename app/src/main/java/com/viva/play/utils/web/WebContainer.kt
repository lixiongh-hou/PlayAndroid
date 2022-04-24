package com.viva.play.utils.web

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
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
class WebContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

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

    init {
        val viewConfiguration = ViewConfiguration.get(context)
        mTouchSlop = viewConfiguration.scaledTouchSlop.toFloat()
        mTapTimeout = ViewConfiguration.getTapTimeout().toLong()
        mDoubleTapTimeout = ViewConfiguration.getDoubleTapTimeout().toLong()
    }

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

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mDownX = event.x
                mDownY = event.y
                mDownTime = System.currentTimeMillis()
                onTouchDown()
            }
            MotionEvent.ACTION_UP -> {
                val upX = event.x
                val upY = event.y
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
            }
        }
        return super.dispatchTouchEvent(event)
    }

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
        heartView.layoutParams = LayoutParams(size, size)
        return heartView
    }
}