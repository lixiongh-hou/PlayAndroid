package com.viva.play.utils

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsAnimation
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat


/**
 * 沉浸式状态栏
 *
 * @param decorFitsSystemWindows 表示是否沉浸，false 表示沉浸，true表示不沉浸
 */
fun Activity.decorFitsSystemWindows(decorFitsSystemWindows: Boolean = true) {
    WindowCompat.setDecorFitsSystemWindows(window, decorFitsSystemWindows)
}

/**
 * 设置专栏栏和导航栏的底色，透明
 */
fun Activity.statusBarColor(
    @ColorInt statusBarColor: Int = Color.TRANSPARENT,
    @ColorInt navigationBarColor: Int? = null,
) {
    window.statusBarColor = statusBarColor
    navigationBarColor?.let { window.navigationBarColor = it }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        navigationBarColor?.let { window.navigationBarDividerColor = it }
    }
}

/**
 * 设置沉浸后状态栏和导航栏体的颜色
 *
 * @param isStatusBarBlack 表示状态栏字体是否是暗色， false 亮色, true 暗色
 * @param isNavigationBarBlack 表示导航栏体是否是暗色， false 亮色, true 暗色
 */
fun Activity.statusBarFontColor(
    isStatusBarBlack: Boolean = false,
    isNavigationBarBlack: Boolean = true
) {
    ViewCompat.getWindowInsetsController(findViewById<FrameLayout>(android.R.id.content))
        ?.let { controller ->
            controller.isAppearanceLightStatusBars = isStatusBarBlack
            controller.isAppearanceLightNavigationBars = isNavigationBarBlack
        }
}

/**
 * 全屏 WindowInsetsCompat.Type.systemBars()表示状态栏、导航栏和标题栏
 *
 */
fun Activity.hideSystemUI() {
    ViewCompat.getWindowInsetsController(findViewById<FrameLayout>(android.R.id.content))
        ?.let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    //此部分解决隐藏状态栏后，水滴屏顶部有黑条
    val lp: WindowManager.LayoutParams = window.attributes
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        lp.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
    }
    window.attributes = lp
}

/**
 * 退出全屏 WindowInsetsCompat.Type.systemBars()表示状态栏、导航栏和标题栏
 *
 */
fun Activity.showSystemUI() {
    ViewCompat.getWindowInsetsController(findViewById<FrameLayout>(android.R.id.content))
        ?.show(WindowInsetsCompat.Type.systemBars())
}

/**
 * 隐藏软键盘
 */
fun Activity.hideSoftKeyboard() {
    ViewCompat.getWindowInsetsController(findViewById<FrameLayout>(android.R.id.content))
        ?.hide(WindowInsetsCompat.Type.ime())
}

/**
 * 显示软键盘
 */
fun Activity.showSoftKeyboard() {
    ViewCompat.getWindowInsetsController(findViewById<FrameLayout>(android.R.id.content))
        ?.show(WindowInsetsCompat.Type.ime())
}

/**
 * 获取软键盘的高度
 */
inline fun Activity.addKeyBordHeightChangeCallBack(view: View, crossinline doAction: (height: Int) -> Unit) {
    var posBottom: Int
    decorFitsSystemWindows(false)//这个一定要添加
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val cb = object : WindowInsetsAnimation.Callback(DISPATCH_MODE_STOP) {
            override fun onProgress(
                insets: WindowInsets,
                runningAnimations: MutableList<WindowInsetsAnimation>
            ): WindowInsets {
//                posBottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom +
//                        insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
                val keyboardHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
                doAction.invoke(keyboardHeight)
                return insets
            }
        }
        view.setWindowInsetsAnimationCallback(cb)
    } else {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
//            posBottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom +
//                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            val keyboardHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            doAction.invoke(keyboardHeight)
            insets
        }
    }
}



