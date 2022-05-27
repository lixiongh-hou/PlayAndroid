@file:Suppress("unused")

package com.viva.play.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

private var sToast: Toast? = null

fun Fragment.toast(@StringRes message: Int, duration: Int = Toast.LENGTH_LONG): Toast =
    requireContext().toast(message, duration)

fun Fragment.toast(message: CharSequence, duration: Int = Toast.LENGTH_LONG): Toast =
    requireContext().toast(message, duration)

fun Context.toast(@StringRes message: Int, duration: Int = Toast.LENGTH_LONG): Toast =
    initToast(message, duration).apply { show() }

fun Context.toast(message: CharSequence, duration: Int = Toast.LENGTH_LONG): Toast =
    initToast(message, duration).apply { show() }

private fun Context.initToast(message: CharSequence, duration: Int = Toast.LENGTH_LONG): Toast {
    cancel()
    sToast = Toast.makeText(this, null, duration).fixBadTokenException()
    sToast?.setText(message)
    sToast?.duration = duration
    return sToast!!
}

private fun Context.initToast(@StringRes message: Int, duration: Int = Toast.LENGTH_LONG): Toast {
    cancel()
    sToast = Toast.makeText(this, null, duration).fixBadTokenException()
    sToast?.setText(message)
    sToast?.duration = duration
    return sToast!!
}

private fun cancel() {
    sToast?.cancel()
    sToast = null
}

private fun Toast.fixBadTokenException(): Toast = apply {
    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
        try {
            @SuppressLint("DiscouragedPrivateApi")
            val tnField = Toast::class.java.getDeclaredField("mTN")
            tnField.isAccessible = true
            val tn = tnField.get(this)

            val handlerField = tnField.type.getDeclaredField("mHandler")
            handlerField.isAccessible = true
            val handler = handlerField.get(tn) as Handler

            val looper = checkNotNull(Looper.myLooper()) {
                "Can't toast on a thread that has not called Looper.prepare()"
            }
            handlerField.set(tn, object : Handler(looper) {
                override fun handleMessage(msg: Message) {
                    try {
                        handler.handleMessage(msg)
                    } catch (ignored: WindowManager.BadTokenException) {
                    }
                }
            })
        } catch (ignored: IllegalAccessException) {
        } catch (ignored: NoSuchFieldException) {
        }
    }
}