package com.viva.play.views.draggable

import android.view.View
import androidx.core.view.ViewCompat

/**
 * @author 李雄厚
 *
 * Utility helper for moving a [View] around using
 * [View.offsetLeftAndRight] and
 * [View.offsetTopAndBottom] and
 * Also the setting of absolute offsets (similar to translationX/Y), rather than additive offsets.
 */
class QMUIViewOffsetHelper(private val view: View) {

    private var mLayoutTop = 0
    private var mLayoutLeft = 0
    private var mOffsetTop = 0
    private var mOffsetLeft = 0

    private var mVerticalOffsetEnabled = true
    private var mHorizontalOffsetEnabled = true

    fun onViewLayout() {
        onViewLayout(true)
    }

    fun onViewLayout(applyOffset: Boolean) {
        mLayoutTop = view.top
        mLayoutLeft = view.left
        if (applyOffset) {
            applyOffsets()
        }
    }

    private fun applyOffsets() {
        ViewCompat.offsetTopAndBottom(view, mOffsetTop - (view.top - mLayoutTop))
        ViewCompat.offsetLeftAndRight(view, mOffsetLeft - (view.left - mLayoutLeft))
    }

    /**
     * Set the top and bottom offset for this [QMUIViewOffsetHelper]'s view.
     *
     * @param offset the offset in px.
     * @return true if the offset has changed
     */
    fun setTopAndBottomOffset(offset: Int): Boolean {
        if (mVerticalOffsetEnabled && mOffsetTop != offset) {
            mOffsetTop = offset
            applyOffsets()
            return true
        }
        return false
    }

    /**
     * Set the left and right offset for this [QMUIViewOffsetHelper]'s view.
     *
     * @param offset the offset in px.
     * @return true if the offset has changed
     */
    fun setLeftAndRightOffset(offset: Int): Boolean {
        if (mHorizontalOffsetEnabled && mOffsetLeft != offset) {
            mOffsetLeft = offset
            applyOffsets()
            return true
        }
        return false
    }

    fun setOffset(leftOffset: Int, topOffset: Int): Boolean {
        return if (!mHorizontalOffsetEnabled && !mVerticalOffsetEnabled) {
            false
        } else if (mHorizontalOffsetEnabled && mVerticalOffsetEnabled) {
            if (mOffsetLeft != leftOffset || mOffsetTop != topOffset) {
                mOffsetLeft = leftOffset
                mOffsetTop = topOffset
                applyOffsets()
                return true
            }
            false
        } else if (mHorizontalOffsetEnabled) {
            setLeftAndRightOffset(leftOffset)
        } else {
            setTopAndBottomOffset(topOffset)
        }
    }

    fun getTopAndBottomOffset(): Int {
        return mOffsetTop
    }

    fun getLeftAndRightOffset(): Int {
        return mOffsetLeft
    }

    fun getLayoutTop(): Int {
        return mLayoutTop
    }

    fun getLayoutLeft(): Int {
        return mLayoutLeft
    }

    fun setHorizontalOffsetEnabled(horizontalOffsetEnabled: Boolean) {
        mHorizontalOffsetEnabled = horizontalOffsetEnabled
    }

    fun isHorizontalOffsetEnabled(): Boolean {
        return mHorizontalOffsetEnabled
    }

    fun setVerticalOffsetEnabled(verticalOffsetEnabled: Boolean) {
        mVerticalOffsetEnabled = verticalOffsetEnabled
    }

    fun isVerticalOffsetEnabled(): Boolean {
        return mVerticalOffsetEnabled
    }

}