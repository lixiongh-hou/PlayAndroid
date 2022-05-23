package com.viva.play.views.draggable

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout

/**
 * @author 李雄厚
 *
 *
 */
open class QMUIViewOffsetBehavior<V : View> : CoordinatorLayout.Behavior<V> {

    private var viewOffsetHelper: QMUIViewOffsetHelper? = null

    private var tempTopBottomOffset = 0
    private var tempLeftRightOffset = 0

    constructor()

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        // First let lay the child out
        layoutChild(parent, child, layoutDirection)

        if (viewOffsetHelper == null) {
            viewOffsetHelper = QMUIViewOffsetHelper(child)
        }
        viewOffsetHelper?.onViewLayout()

        if (tempTopBottomOffset != 0) {
            viewOffsetHelper?.setTopAndBottomOffset(tempTopBottomOffset)
            tempTopBottomOffset = 0
        }

        if (tempLeftRightOffset != 0) {
            viewOffsetHelper?.setLeftAndRightOffset(tempLeftRightOffset)
            tempLeftRightOffset = 0
        }
        return true
    }

    open fun layoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int) {
        // Let the parent lay it out by default
        parent.onLayoutChild(child, layoutDirection)
    }

    open fun setTopAndBottomOffset(offset: Int): Boolean {
        tempTopBottomOffset = if (viewOffsetHelper != null) {
            return viewOffsetHelper!!.setTopAndBottomOffset(offset)
        } else {
            offset
        }
        return false
    }

    open fun setLeftAndRightOffset(offset: Int): Boolean {
        tempLeftRightOffset = if (viewOffsetHelper != null) {
            return viewOffsetHelper!!.setLeftAndRightOffset(offset)
        } else {
            offset
        }
        return false
    }

    fun getTopAndBottomOffset(): Int {
        return if (viewOffsetHelper != null) viewOffsetHelper!!.getTopAndBottomOffset() else 0
    }

    fun getLeftAndRightOffset(): Int {
        return if (viewOffsetHelper != null) viewOffsetHelper!!.getLeftAndRightOffset() else 0
    }

    fun setVerticalOffsetEnabled(verticalOffsetEnabled: Boolean) {
        if (viewOffsetHelper != null) {
            viewOffsetHelper!!.setVerticalOffsetEnabled(verticalOffsetEnabled)
        }
    }

    fun getLayoutTop(): Int {
        return if (viewOffsetHelper != null) {
            viewOffsetHelper!!.getLayoutTop()
        } else 0
    }

    fun getLayoutLeft(): Int {
        return if (viewOffsetHelper != null) {
            viewOffsetHelper!!.getLayoutLeft()
        } else 0
    }

    fun isVerticalOffsetEnabled(): Boolean {
        return viewOffsetHelper != null && viewOffsetHelper!!.isVerticalOffsetEnabled()
    }

    fun setHorizontalOffsetEnabled(horizontalOffsetEnabled: Boolean) {
        if (viewOffsetHelper != null) {
            viewOffsetHelper!!.setHorizontalOffsetEnabled(horizontalOffsetEnabled)
        }
    }

    fun isHorizontalOffsetEnabled(): Boolean {
        return viewOffsetHelper != null && viewOffsetHelper!!.isHorizontalOffsetEnabled()
    }


}