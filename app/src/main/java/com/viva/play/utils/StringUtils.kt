package com.viva.play.utils

import android.text.Html
import java.lang.StringBuilder
import java.util.regex.Pattern

/**
 * @author 李雄厚
 *
 *
 */

fun String?.removeAllBank(count: Int): String {
    var s = ""
    if (!this.isNullOrEmpty()) {
        val p = Pattern.compile("\\s{$count,}|\t|\r|\n")
        val m = p.matcher(this)
        s = m.replaceAll(" ")
    }
    return s
}

fun String?.formatHtml(): String {
    var s = ""
    if (!this.isNullOrEmpty()) {
        s = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY).toString()
        } else {
            Html.fromHtml(this).toString()
        }
    }
    return s
}

object StringUtils {


    @JvmStatic
    fun <T> isNullOrEmpty(data: List<T>?): Boolean {
        return data.isNullOrEmpty()
    }

    @JvmStatic
    fun formatHtml(str: String?): String {
        var s = ""
        if (!str.isNullOrEmpty()) {
            s = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY).toString()
            } else {
                Html.fromHtml(str).toString()
            }
        }
        return s
    }

    @JvmStatic
    fun removeAllBank(str: String?, count: Int): String {
        var s = ""
        if (!str.isNullOrEmpty()) {
            val p = Pattern.compile("\\s{$count,}|\t|\r|\n")
            val m = p.matcher(str)
            s = m.replaceAll(" ")
        }
        return s
    }

    @JvmStatic
    fun formatChapterName(vararg names: String?): String {
        val format = StringBuilder()
        if (names.isNullOrEmpty()){
            return ""
        }
        names.forEach {
            if (!it.isNullOrEmpty()) {
                if (format.isNotEmpty()) {
                    format.append("·")
                }
                format.append(it)
            }
        }
        return format.toString()
    }
}


