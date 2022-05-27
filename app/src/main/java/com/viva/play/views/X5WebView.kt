package com.viva.play.views

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.ScrollingView
import com.tencent.smtt.sdk.WebView

/**
 * @author 李雄厚
 *
 *
 */
class X5WebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : WebView(context, attrs), ScrollingView {

    override fun canScrollHorizontally(direction: Int): Boolean {
        return view.canScrollHorizontally(direction)
    }

    override fun canScrollVertically(direction: Int): Boolean {
        return view.canScrollVertically(direction)
    }
}