package com.viva.play.views.bottomdrawer

import android.view.View
import androidx.core.view.ScrollingView

object ScrollCompat {

    fun canScrollHorizontally(v: View, direction: Int): Boolean {
        return if (v is ScrollingView) {
            canScrollingViewScrollHorizontally(v as ScrollingView, direction)
        } else {
            v.canScrollHorizontally(direction)
        }
    }

    fun canScrollVertically(v: View, direction: Int): Boolean {
        return if (v is ScrollingView) {
            canScrollingViewScrollVertically(v as ScrollingView, direction)
        } else {
            v.canScrollVertically(direction)
        }
    }

    private fun canScrollingViewScrollHorizontally(view: ScrollingView, direction: Int): Boolean {
        val offset = view.computeHorizontalScrollOffset()
        val range = view.computeHorizontalScrollRange() - view.computeHorizontalScrollExtent()
        if (range == 0) return false
        return if (direction < 0) {
            offset > 0
        } else {
            offset < range - 1
        }
    }

    private fun canScrollingViewScrollVertically(view: ScrollingView, direction: Int): Boolean {
        val offset = view.computeVerticalScrollOffset()
        val range = view.computeVerticalScrollRange() - view.computeVerticalScrollExtent()
        if (range == 0) return false
        return if (direction < 0) {
            offset > 0
        } else {
            offset < range - 1
        }
    }
}
