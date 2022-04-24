package com.viva.play.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.viva.play.App

/**
 * @author 李雄厚
 *
 *
 */
object CopyUtils {

    fun copyText(text: String) {
        val clipboardManager = App.instance.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text",text)
        clipboardManager.setPrimaryClip(clipData)
    }
}