package com.viva.play.utils

import android.animation.ObjectAnimator
import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalFocusChangeListener
import android.view.Window
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import java.util.HashMap

/**
 * @author 李雄厚
 *
 *
 */
class SoftInputHelper(activity: Activity) : ViewTreeObserver.OnGlobalLayoutListener,
    OnGlobalFocusChangeListener {

    private var window: Window = activity.window
    private var rootView: View = window.decorView.rootView

    private var duration = 300L
    private var moveView: View? = null
    private val focusBottomMap = HashMap<View, View>(1)
    private var onSoftInputListener: OnSoftInputListener? = null

    private var moveWithScroll = false

    private var isOpened = false
    private var moveHeight = 0
    private var isFocusChange = false

    private val moveRunnable = Runnable {
        calcToMove()
    }

    companion object {
        fun attach(activity: Activity): SoftInputHelper = SoftInputHelper(activity)
    }

    init {
        val observer = rootView.viewTreeObserver
        observer.addOnGlobalLayoutListener(this)
        observer.addOnGlobalFocusChangeListener(this)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    fun detach() {
        if (rootView.viewTreeObserver.isAlive) {
            rootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            rootView.viewTreeObserver.removeOnGlobalFocusChangeListener(this)
        }
    }

    fun moveBy(moveView: View): SoftInputHelper {
        this.moveView = moveView
        return this
    }

    fun moveWith(bottomView: View, vararg focusViews: View): SoftInputHelper {
        for (focusView in focusViews) {
            focusBottomMap[focusView] = bottomView
        }
        return this
    }

    fun listener(listener: OnSoftInput.() -> Unit) {
        onSoftInputListener = OnSoftInput().also(listener)
    }

    fun duration(duration: Long): SoftInputHelper {
        this.duration = duration
        return this
    }

    /**
     * 设置moveView移动以ScrollY属性滚动内容
     *
     * @return SoftInputHelper
     */
    fun moveWithScroll(): SoftInputHelper {
        moveWithScroll = true
        return this
    }

    /**
     * 设置moveView移动以TranslationY属性移动位置
     *
     * @return SoftInputHelper
     */
    fun moveWithTranslation(): SoftInputHelper {
        moveWithScroll = false
        return this
    }


    override fun onGlobalLayout() {
        val isOpen: Boolean = isSoftOpen()
        if (isOpen) {
            if (!isOpened) {
                isOpened = true
                onSoftInputListener?.onOpen()
            }
            if (moveView != null) {
                if (isFocusChange) {
                    isFocusChange = false
                    rootView.removeCallbacks(moveRunnable)
                }
                calcToMove()
            }
        } else {
            if (isOpened) {
                isOpened = false
                onSoftInputListener?.onClose()
            }

            if (moveView != null) {
                moveHeight = 0
                move()
            }
        }
    }

    private fun calcToMove() {
        val focusView = isViewFocus()
        if (focusView != null) {
            val bottomView = focusBottomMap[focusView]
            if (bottomView != null) {
                val rect = getRootViewRect()
                val bottomViewY = getBottomViewY(bottomView)
                if (bottomViewY > rect.bottom) {
                    val offHeight = bottomViewY - rect.bottom
                    moveHeight += offHeight
                    move()
                } else if (bottomViewY < rect.bottom) {
                    val offHeight = -(bottomViewY - rect.bottom)
                    if (moveHeight > 0) {
                        if (moveHeight >= offHeight) {
                            moveHeight -= offHeight
                        } else {
                            moveHeight = 0
                        }
                        move()
                    }
                }
            }
        } else {
            moveHeight = 0
            move()
        }
    }

    override fun onGlobalFocusChanged(oldFocus: View?, newFocus: View?) {
        if (isOpened){
            if (moveView != null){
                isFocusChange = true
                rootView.postDelayed(moveRunnable, 100)
            }
        }
    }

    private fun getBottomViewY(bottomView: View): Int {
        val bottomLocation = IntArray(2)
        bottomView.getLocationOnScreen(bottomLocation)
        return bottomLocation[1] + bottomView.height
    }

    private fun getRootViewRect(): Rect {
        val rect = Rect()
        rootView.getWindowVisibleDisplayFrame(rect)
        return rect
    }

    private fun move() {
        if (moveWithScroll) {
            scrollTo(moveHeight)
        } else {
            translationTo(-moveHeight)
        }
    }

    private fun translationTo(to: Int) {
        val translationY = moveView!!.translationY
        if (translationY == to.toFloat()) {
            return
        }
        val anim = ObjectAnimator.ofFloat(moveView!!, "translationY", translationY, to.toFloat())
        anim.interpolator = DecelerateInterpolator()
        anim.duration = duration
        anim.start()

    }

    private fun scrollTo(to: Int) {
        val scrollY = moveView!!.scrollY
        if (scrollY == to) {
            return
        }
        val anim = ObjectAnimator.ofInt(moveView!!, "scrollY", scrollY, to)
        anim.interpolator = DecelerateInterpolator()
        anim.duration = duration
        anim.start()
    }

    /**
     * 判断软键盘打开状态的阈值
     * 此处以用户可用高度变化值大于1/4总高度时作为判断依据。
     *
     * @return boolean
     */
    private fun isSoftOpen(): Boolean {
        val rect = getRootViewRect()
        val usableHeightNow = rect.bottom - rect.top
        val usableHeightSansKeyboard = rootView.height
        val heightDifference = usableHeightSansKeyboard - usableHeightNow
        return heightDifference > usableHeightSansKeyboard / 4
    }

    private fun isViewFocus(): View? {
        val focusView = window.currentFocus
        for (view in focusBottomMap.keys) {
            if (focusView === view) {
                return view
            }
        }
        return null
    }


    class OnSoftInput : OnSoftInputListener {
        private lateinit var open: () -> Unit
        private lateinit var close: () -> Unit

        fun onOpen(open: () -> Unit) {
            this.open = open
        }

        fun onClose(close: () -> Unit) {
            this.close = close
        }

        override fun onOpen() {
            open()
        }

        override fun onClose() {
            close()
        }

    }

    interface OnSoftInputListener {
        /**
         * 软键盘由关闭变为打开时调用
         */
        fun onOpen()

        /**
         * 软键盘由打开变为关闭时调用
         */
        fun onClose()
    }
}