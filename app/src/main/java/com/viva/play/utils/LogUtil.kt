package com.viva.play.utils

import android.util.Log
import androidx.annotation.IntDef

/**
 * @author 李雄厚
 *
 *
 */
object LogUtil {

    // 是否打印Log
    private const val DEBUG = true

    fun e(tag: String, msg: String) {
        if (DEBUG) {
            Log.e(tag, msg)
        }
    }

    fun e(msg: String) {
        log(E, false, msg)
    }

    fun i(msg: String) {
        log(I, false, msg)
    }

    fun eSuper(msg: String) {
        log(E, true, msg)
    }

    private const val I = 1
    private const val W = 2
    private const val E = 3

    @IntDef(I, W, E)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class TYPE

    private const val SOLID_LINE = "────────────────────────────────────────────────────────────"
    private const val DOTTED_LINE = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄"
    private const val TOP_LINE = "┌$SOLID_LINE$SOLID_LINE"
    private const val MIDDLE_LINE = "├$DOTTED_LINE$DOTTED_LINE"
    private const val BOTTOM_LINE = "└$SOLID_LINE$SOLID_LINE"

    private const val HEAD_LINE = "│ "
    private const val MAX_LENGTH = 3000

    private fun log(@TYPE type: Int, isSuper: Boolean, msg: String) {
        if (!DEBUG) {
            return
        }

        val stackTrace = Throwable().stackTrace
        val ste = stackTrace[2]
        // 文件名
        val fileNameLong = ste.fileName
        val fileName = fileNameLong.substring(0, fileNameLong.lastIndexOf("."))
        // 行数
        val lineNumber = ste.lineNumber
        // 线程名
        val threadName = Thread.currentThread().name
        print(type, "${getRandom()}\u3000$fileName", TOP_LINE)
        if (isSuper) {
            print(
                type, "${getRandom()}\u3000$fileName",
                "${HEAD_LINE}Thread:${threadName}\u3000(${fileNameLong}:${lineNumber})"
            )
            print(type, "${getRandom()}\u3000$fileName", MIDDLE_LINE)
        }
        val msgLength = msg.length
        var start = 0
        var end: Int = MAX_LENGTH
        run B@{
            (0 until 100).forEach { _ ->
                if (msgLength > end) {
                    print(
                        type,
                        "${getRandom()}\u3000$fileName",
                        HEAD_LINE + msg.substring(start, end)
                    )
                    start = end
                    end += MAX_LENGTH
                } else {
                    print(
                        type,
                        "${getRandom()}\u3000$fileName",
                        HEAD_LINE + msg.substring(start, msgLength)
                    )
                    return@B
                }
            }
        }
        print(type, "${getRandom()}\u3000$fileName", BOTTOM_LINE)

    }

    private fun print(@TYPE type: Int, tag: String, msg: String) {
        when (type) {
            I -> {
                Log.i(tag, msg)
            }
            W -> {
                Log.w(tag, msg)
            }
            E -> {
                Log.e(tag, msg)
            }
        }
    }


    private fun getRandom(): Int {
        return ((Math.random() * 9 + 1) * 10000).toInt()
    }
}