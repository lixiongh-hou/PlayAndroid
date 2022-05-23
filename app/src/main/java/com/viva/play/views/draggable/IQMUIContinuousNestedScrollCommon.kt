package com.viva.play.views.draggable

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @author 李雄厚
 *
 *
 */
interface IQMUIContinuousNestedScrollCommon {

    companion object{
        var SCROLL_STATE_IDLE = RecyclerView.SCROLL_STATE_IDLE
        var SCROLL_STATE_DRAGGING = RecyclerView.SCROLL_STATE_DRAGGING
        var SCROLL_STATE_SETTLING = RecyclerView.SCROLL_STATE_SETTLING
    }

    fun injectScrollNotifier(notifier: OnScrollNotifier)

    interface OnScrollNotifier {
        fun notify(innerOffset: Int, innerRange: Int)
        fun onScrollStateChange(view: View, newScrollState: Int)
    }
}