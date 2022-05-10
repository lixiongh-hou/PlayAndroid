package com.viva.play.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * @author 李雄厚
 *
 *
 */
object DataUtil {

    /**
     * 格式化Date
     * 中文显示 2019年7月22日 星期一
     * 英文显示 Monday, Jul 22, 2019
     */
    @JvmStatic
    fun useBeautifulFormat(data: Date): String {
        val language = "zh"
        return if (language == "zh") {
            val dateFormat = SimpleDateFormat("yyyy年M月d日 HH:mm:ss EEEE", Locale.CHINA)
            dateFormat.format(data)
        } else {
            val dateFormat = SimpleDateFormat("EEEE MMM d HH:mm:ss 'EST' yyyy ", Locale.ENGLISH)
            dateFormat.format(data)
        }
    }

    @JvmStatic
    fun useBeautifulSSFormat(data: Date?):String {
        if (data == null){
            return ""
        }
        val dateFormat = SimpleDateFormat("yyyy年M月d日 HH:mm:ss", Locale.CHINA)
       return dateFormat.format(data)
    }
}