package com.viva.play.views.draggable

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.viva.play.R
import com.viva.play.utils.dpToPx

/**
 * @author 李雄厚
 *
 *
 */
open class QMUIDraggableScrollBar : View {

    companion object {
        private val STATE_PRESSED = intArrayOf(android.R.attr.state_pressed)
        private val STATE_NORMAL = intArrayOf()
    }

    private var mDragDrawable: Drawable? = null
    private var mKeepShownTime = 800
    private var mTransitionDuration = 100
    private var mStartTransitionTime = 0L
    private var mCurrentAlpha = 0.0F
    private var mPercent = 0.0F
    private val mDelayInvalidateRunnable = Runnable {
        invalidate()
    }
    private var mIsInDragging = false
    private var mCallback: Callback? = null
    private var mDrawableDrawTop = -1
    private var mDragInnerTop = 0.0F
    private var mAdjustDistanceProtection = 20.dpToPx
    private var mAdjustMaxDistanceOnce = 4.dpToPx
    private var mAdjustDistanceWithAnimation = true
    private var enableFadeInAndOut = true

    constructor(context: Context) : this(context, null)

    constructor(context: Context, dragDrawable: Drawable) : this(context, null) {
        mDragDrawable = dragDrawable.mutate()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    fun setCallback(callback: Callback) {
        mCallback = callback
    }

    fun setAdjustDistanceWithAnimation(adjustDistanceWithAnimation: Boolean) {
        mAdjustDistanceWithAnimation = adjustDistanceWithAnimation
    }

    fun setKeepShownTime(keepShownTime: Int) {
        mKeepShownTime = keepShownTime
    }

    fun setTransitionDuration(transitionDuration: Int) {
        mTransitionDuration = transitionDuration
    }

    /**
     * 设置淡入淡出
     */
    fun setEnableFadeInAndOut(enableFadeInAndOut: Boolean) {
        this.enableFadeInAndOut = enableFadeInAndOut
    }

    fun isEnableFadeInAndOut(): Boolean = enableFadeInAndOut

    fun setDragDrawable(dragDrawable: Drawable) {
        mDragDrawable = dragDrawable.mutate()
        invalidate()
    }

    fun setPercent(percent: Float) {
        if (!mIsInDragging) {
            setPercentInternal(percent)
        }
    }

    private fun setPercentInternal(percent: Float) {
        mPercent = percent
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val drawable = mDragDrawable ?: return super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(drawable.intrinsicWidth, MeasureSpec.EXACTLY),
            heightMeasureSpec
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val drawable = mDragDrawable ?: return super.onTouchEvent(event)
        val action = event?.action ?: return super.onTouchEvent(event)
        val x = event.x
        val y = event.y
        if (action == MotionEvent.ACTION_DOWN) {
            mIsInDragging = false
            if (mCurrentAlpha > 0 && x > width - drawable.intrinsicWidth && y >= mDrawableDrawTop && y <= mDrawableDrawTop + drawable.intrinsicHeight) {
                mDragInnerTop = y - mDrawableDrawTop
                parent.requestDisallowInterceptTouchEvent(true)
                mIsInDragging = true
                if (mCallback != null) {
                    mCallback!!.onDragStarted()
                    mDragDrawable?.state = STATE_PRESSED
                }
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (mIsInDragging) {
                parent.requestDisallowInterceptTouchEvent(true)
                onDragging(drawable, y)
            }
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            if (mIsInDragging) {
                mIsInDragging = false
                onDragging(drawable, y)
                if (mCallback != null) {
                    mCallback?.onDragEnd()
                    mDragDrawable?.state = STATE_NORMAL
                }
            }
        }
        return mIsInDragging
    }

    private fun onDragging(drawable: Drawable, currentY: Float) {
        var percent =
            (currentY - getScrollBarTopMargin() - mDragInnerTop) / (height - getScrollBarBottomMargin() - getScrollBarTopMargin() - drawable.intrinsicHeight)
        percent = constrain(percent)
        mCallback?.onDragToPercent(percent)
        setPercentInternal(percent)
    }

    fun awakenScrollBar() {
        if (mDragDrawable == null) {
            mDragDrawable = ContextCompat.getDrawable(context, R.drawable.qmui_icon_scroll_bar)
        }
        val current = System.currentTimeMillis()
        if (current - mStartTransitionTime > mTransitionDuration) {
            mStartTransitionTime = current - mTransitionDuration
        }
        ViewCompat.postInvalidateOnAnimation(this)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val drawable = mDragDrawable ?: return
        val drawableWidth = drawable.intrinsicWidth
        val drawableHeight = drawable.intrinsicHeight
        if (drawableWidth <= 0 || drawableHeight <= 0) {
            return
        }
        var needInvalidate = -1
        if (enableFadeInAndOut) {
            val timeAfterStartShow = System.currentTimeMillis() - mStartTransitionTime
            var timeAfterEndShow: Long
            if (timeAfterStartShow < mTransitionDuration) {
                // in show animation
                mCurrentAlpha = timeAfterStartShow * 1F / mTransitionDuration
                needInvalidate = 0
            } else if (timeAfterStartShow - mTransitionDuration < mKeepShownTime) {
                // keep show
                mCurrentAlpha = 1F
                needInvalidate =
                    (mKeepShownTime - (timeAfterStartShow - mTransitionDuration)).toInt()
            } else if (timeAfterStartShow - mTransitionDuration - mKeepShownTime.also {
                    timeAfterEndShow = it.toLong()
                } < mTransitionDuration) {
                // in hide animation
                mCurrentAlpha = 1 - timeAfterEndShow * 1f / mTransitionDuration
                needInvalidate = 0
            } else {
                mCurrentAlpha = 0F
            }
            if (mCurrentAlpha <= 0F) {
                return
            }
        } else {
            mCurrentAlpha = 1F
        }
        drawable.alpha = (mCurrentAlpha * 255).toInt()

        val totalHeight = height - getScrollBarTopMargin() - getScrollBarBottomMargin()
        val totalWidth = width
        var top = getScrollBarTopMargin() + ((totalHeight - drawableHeight) * mPercent).toInt()
        val left = totalWidth - drawableWidth
        if (!mIsInDragging && mDrawableDrawTop > 0 && mAdjustDistanceWithAnimation) {
            val moveDistance = top - mDrawableDrawTop
            if (moveDistance in (mAdjustMaxDistanceOnce + 1) until mAdjustDistanceProtection) {
                top = mDrawableDrawTop + mAdjustMaxDistanceOnce
                needInvalidate = 0
            } else if (moveDistance < -mAdjustMaxDistanceOnce && moveDistance > -mAdjustDistanceProtection) {
                top = mDrawableDrawTop - mAdjustMaxDistanceOnce
                needInvalidate = 0
            }
        }
        drawable.setBounds(0, 0, drawableWidth, drawableHeight)
        canvas?.save()
        canvas?.translate(left.toFloat(), top.toFloat())
        drawable.draw(canvas!!)
        canvas.restore()
        mDrawableDrawTop = top

        if (needInvalidate == 0) {
            invalidate()
        } else if (needInvalidate > 0) {
            ViewCompat.postOnAnimationDelayed(
                this,
                mDelayInvalidateRunnable,
                needInvalidate.toLong()
            )
        }
    }

    open fun getScrollBarTopMargin(): Int = 0
    open fun getScrollBarBottomMargin(): Int = 0

    private fun constrain(amount: Float, low: Float = 0F, high: Float = 1F): Float {
        return if (amount < low) low else amount.coerceAtMost(high)
    }

    interface Callback {
        fun onDragStarted()
        fun onDragToPercent(percent: Float)
        fun onDragEnd()
    }
}