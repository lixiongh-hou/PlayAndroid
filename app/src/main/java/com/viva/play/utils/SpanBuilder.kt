package com.viva.play.utils

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

private const val NO_DATA = -1

/**
 * @author 李雄厚
 *
 * @features SpannableStringBuilder工具类
 *
 * val string = SpanBuilder().apply {
 *  append("已到")
 *  append(content)
 *  setForegroundColor(Color.RED)
 *  append("检测时间段\n")
 * }.create()
 */

class SpanBuilder {

    private var mBuilder = SpannableStringBuilder()

    // 临时保存文本
    private var mText: String = ""

    // 前景颜色
    @ColorInt
    private var mForegroundColor = 0

    // 字体比例
    private var mProportion = 0F

    init {
        reset()
    }

    /**
     * 附加文本
     */
    fun append(text: String) {
        apply()
        mText += text
    }

    /**
     * 应用
     */
    private fun apply() {
        if (mText.isEmpty()) {
            return
        }
        val start = mBuilder.length
        mBuilder.append(mText)
        val end = mBuilder.length
        // 前景颜色
        if (NO_DATA != mForegroundColor) {
            mBuilder.setSpan(
                ForegroundColorSpan(mForegroundColor),
                start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }
        // 字体比例
        if (NO_DATA.toFloat() != mProportion) {
            mBuilder.setSpan(
                RelativeSizeSpan(mProportion),
                start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }
        // 重置
        reset()
    }

    /**
     * 重置
     */
    private fun reset() {
        mText = ""
        mForegroundColor = NO_DATA
        mProportion = NO_DATA.toFloat()
    }

    /**
     * 前景颜色
     */
    fun setForegroundColor(@ColorInt colorInt: Int) {
        mForegroundColor = colorInt
    }

    /**
     * 前景颜色
     */
    fun setForegroundColor(context: Context, @ColorRes colorRes: Int) {
        mForegroundColor = ContextCompat.getColor(context, colorRes)
    }

    /**
     * 字体比例
     */
    fun setProportion(proportion: Float) {
        mProportion = proportion
    }

    /**
     * 创建
     */
    fun create(): SpannableStringBuilder {
        apply()
        return mBuilder
    }

}