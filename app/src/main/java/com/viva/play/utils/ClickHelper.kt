package com.viva.play.utils

import android.view.View
import java.util.ArrayList

/**
 * @author 李雄厚
 *
 *
 */
object ClickHelper {

    private var DELAY = 500L
    private var lastTime = 0L
    private var viewIds: ArrayList<Int>? = null
    private const val SAME_VIEW_SIZE = 10

    fun View.onClickListener2(callback: (view: View) -> Unit) {
        setOnClickListener { target ->
            val nowTime = System.currentTimeMillis()
            val id = target.id
            if (viewIds == null) {
                viewIds = ArrayList(SAME_VIEW_SIZE)
            }
            if (viewIds!!.contains(id)) {
                if (nowTime - lastTime > DELAY) {
                    callback.invoke(target)
                    lastTime = nowTime
                }
            } else {
                if (viewIds!!.size >= SAME_VIEW_SIZE) {
                    viewIds!!.removeAt(0)
                }
                viewIds!!.add(id)
                callback.invoke(target)
                lastTime = nowTime
            }
        }
    }
}
