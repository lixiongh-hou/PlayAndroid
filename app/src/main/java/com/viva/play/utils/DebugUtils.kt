package com.viva.play.utils

import android.text.TextUtils
import com.viva.play.BuildConfig

/**
 * @author 李雄厚
 *
 *
 */
object DebugUtils {

    fun isDebug(): Boolean {
        return BuildConfig.DEBUG && TextUtils.equals(BuildConfig.BUILD_TYPE, "debug")
    }
}