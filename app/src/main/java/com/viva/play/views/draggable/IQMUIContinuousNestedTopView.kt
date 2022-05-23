package com.viva.play.views.draggable

/**
 * @author 李雄厚
 *
 *
 */
interface IQMUIContinuousNestedTopView: IQMUIContinuousNestedScrollCommon {
    /**
     * consume scroll
     *
     * @param dyUnconsumed the delta value to consume
     * @return the remain unconsumed value
     */
    fun consumeScroll(dyUnconsumed: Int): Int

    fun getCurrentScroll(): Int

    fun getScrollOffsetRange(): Int
}