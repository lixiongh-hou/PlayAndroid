package com.viva.play.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * @author 李雄厚
 *
 *
 */
object InputMethodUtils {

    /**
     * 隐藏虚拟键盘
     */
    fun hide(v: View) {
        val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.applicationWindowToken, 0)
    }

    /**
     * 显示虚拟键盘
     */
    fun show(v: View) {
        val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(v, 0)
    }
}