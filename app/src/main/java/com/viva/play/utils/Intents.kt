@file:Suppress("unused")

package com.viva.play.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner

/**
 * @author 李雄厚
 *
 *
 */

/*------------------------------------Activity-------------------------------------------------------*/
inline fun <reified T> Context.intentOf(vararg pair: Pair<String, *>): Intent =
    intentOf<T>(bundleOf(*pair))

inline fun <reified T> Context.intentOf(bundle: Bundle): Intent =
    Intent(this, T::class.java).putExtras(bundle)

@Suppress("UNCHECKED_CAST")
fun <T> AppCompatActivity.intentExtras(key: String) = lazy<T?> {
    intent.extras?.get(key) as T
}

fun <T> AppCompatActivity.intentExtras(key: String, default: T) = lazy {
    intent.extras?.get(key) ?: default
}

@Suppress("UNCHECKED_CAST")
fun <T> AppCompatActivity.safeIntentExtras(key: String) = lazy<T> {
    checkNotNull(intent.extras?.get(key) as T) { "No intent value for key \"$key\"" }
}


fun AppCompatActivity.pressBackTwiceToExitApp(
    @StringRes toastText: Int,
    delayMillis: Long = 2000,
    owner: LifecycleOwner = this
) = pressBackTwiceToExitApp(delayMillis, owner) { toast(toastText) }

fun AppCompatActivity.pressBackTwiceToExitApp(
    toastText: String,
    delayMillis: Long = 2000,
    owner: LifecycleOwner = this
) = pressBackTwiceToExitApp(delayMillis, owner) { toast(toastText) }

fun AppCompatActivity.pressBackTwiceToExitApp(
    delayMillis: Long = 2000L,
    owner: LifecycleOwner = this,
    onFirstBackPressed: () -> Unit
) {
    onBackPressedDispatcher.addCallback(owner, object : OnBackPressedCallback(true) {
        private var lastBackTime: Long = 0L
        override fun handleOnBackPressed() {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastBackTime > delayMillis) {
                onFirstBackPressed()
                lastBackTime = currentTime
            } else {
                finish()
            }
        }
    })
}

/*------------------------------------Fragment-------------------------------------------------------*/
fun <T : Fragment> T.withArguments(vararg pairs: Pair<String, *>) = apply {
    arguments = bundleOf(*pairs)
}

@Suppress("UNCHECKED_CAST")
fun <T> Fragment.arguments(key: String) = lazy {
    arguments?.get(key) as T
}

@Suppress("UNCHECKED_CAST")
fun <T> Fragment.arguments(key: String, default: T) = lazy {
    (arguments?.get(key) ?: default) as T
}

@Suppress("UNCHECKED_CAST")
fun <T> Fragment.safeArguments(key: String) = lazy<T> {
    checkNotNull(arguments?.get(key) as T) { "No intent value for key \"$key\"" }
}
