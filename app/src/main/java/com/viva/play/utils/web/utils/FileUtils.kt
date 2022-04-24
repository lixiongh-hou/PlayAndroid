package com.viva.play.utils.web.utils

import android.os.Environment
import android.text.TextUtils
import java.io.File
import java.lang.Exception
import java.math.BigDecimal

/**
 * @author 李雄厚
 *
 *
 */
object FileUtils {

    fun isSDCardAlive(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    fun delete(file: File?, except: String) {
        if (file == null) {
            return
        }
        if (file.isDirectory) {
            val children = file.list() ?: return
            for (c in children) {
                val childFile = File(file, c)
                if (!TextUtils.equals(childFile.name, except)) {
                    delete(childFile)
                }
            }
        } else {
            if (!TextUtils.equals(file.name, except)) {
                file.delete()
            }
        }
    }

    fun delete(file: File?): Boolean {
        if (file == null) {
            return false
        }
        if (file.isDirectory) {
            val children = file.list() ?: return false
            for (c in children) {
                val success = delete(File(file, c))
                if (!success) {
                    return false
                }
            }
        }
        return file.delete()
    }

    fun getSize(file: File?): Long {
        var size = 0L
        try {
            val fileList = file?.listFiles() ?: return 0L
            for (f in fileList) {
                size = if (f.isDirectory) {
                    size + getSize(f)
                } else {
                    size + f.length()
                }
            }
        } catch (ignore: Exception) {
        }
        return size
    }

    /**
     * 格式化单位
     */
    fun formatSize(size: Double): String {
        val kiloByte = size / 1024
        if (kiloByte < 1) {
            return "0KB"
        }
        val megaByte = kiloByte / 1024
        if (megaByte < 1) {
            val result1 = BigDecimal(kiloByte.toString())
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB"
        }
        val gigaByte = megaByte / 1024
        if (gigaByte < 1) {
            val result2 = BigDecimal(megaByte.toString())
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB"
        }
        val teraBytes = gigaByte / 1024
        if (teraBytes < 1) {
            val result3 = BigDecimal(gigaByte.toString())
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB"
        }
        val result4 = BigDecimal(teraBytes)
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB"
    }
}