/*
 * Copyright (c) 2021. Dylan Cai
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("unused")

package com.viva.play.utils

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.view.*
import androidx.fragment.app.Fragment
import com.viva.play.R
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun Fragment.immerseStatusBar(lightMode: Boolean = true) {
    activity?.immerseStatusBar(lightMode)
}

fun Activity.immerseStatusBar(lightMode: Boolean = true) {
    decorFitsSystemWindows = false
    window.decorView.windowInsetsControllerCompat?.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    transparentStatusBar()
    isLightStatusBar = lightMode
}

inline var Fragment.isLightStatusBar: Boolean
    get() = activity?.isLightStatusBar == true
    set(value) {
        view?.post { activity?.isLightStatusBar = value }
    }

inline var Activity.isLightStatusBar: Boolean
    get() = window.decorView.windowInsetsControllerCompat?.isAppearanceLightStatusBars == true
    set(value) {
        window.decorView.windowInsetsControllerCompat?.isAppearanceLightStatusBars = value
    }

inline var Fragment.statusBarColor: Int
    get() = activity?.statusBarColor ?: -1
    set(value) {
        activity?.statusBarColor = value
    }

@setparam:ColorInt
inline var Activity.statusBarColor: Int
    get() = window.statusBarColor
    set(value) {
        window.statusBarColor = value
    }

fun Fragment.transparentStatusBar() {
    activity?.transparentStatusBar()
}

fun Activity.transparentStatusBar() {
    statusBarColor = Color.TRANSPARENT
}

var Activity.decorFitsSystemWindows: Boolean
    @Deprecated(NO_GETTER, level = DeprecationLevel.ERROR)
    get() = noGetter()
    set(value) = WindowCompat.setDecorFitsSystemWindows(window, value)

internal const val NO_GETTER: String = "Property does not have a getter"
internal fun noGetter(): Nothing = throw NotImplementedError(NO_GETTER)
internal var View.isAddedMarginTop: Boolean? by viewTags(R.id.tag_is_added_margin_top)
internal var View.isAddedPaddingTop: Boolean? by viewTags(R.id.tag_is_added_padding_top)
internal var View.isAddedMarginBottom: Boolean? by viewTags(R.id.tag_is_added_margin_bottom)
internal var View.lastClickTime: Long? by viewTags(R.id.tag_last_click_time)
internal var View.rootWindowInsetsCompatCache: WindowInsetsCompat? by viewTags(R.id.tag_root_window_insets)
internal var View.windowInsetsControllerCompatCache: WindowInsetsControllerCompat? by viewTags(R.id.tag_window_insets_controller)

fun <T> viewTags(key: Int) = object : ReadWriteProperty<View, T?> {
    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: View, property: KProperty<*>) =
        thisRef.getTag(key) as? T

    override fun setValue(thisRef: View, property: KProperty<*>, value: T?) =
        thisRef.setTag(key, value)
}

val View.windowInsetsControllerCompat: WindowInsetsControllerCompat?
    get() {
        if (windowInsetsControllerCompatCache == null) {
            windowInsetsControllerCompatCache = ViewCompat.getWindowInsetsController(this)
        }
        return windowInsetsControllerCompatCache
    }

val View.rootWindowInsetsCompat: WindowInsetsCompat?
    get() {
        if (rootWindowInsetsCompatCache == null) {
            rootWindowInsetsCompatCache = ViewCompat.getRootWindowInsets(this)
        }
        return rootWindowInsetsCompatCache
    }

inline val Activity.contentView: View
    get() = (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)
