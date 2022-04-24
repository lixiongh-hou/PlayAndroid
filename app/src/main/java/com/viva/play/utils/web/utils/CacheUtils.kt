package com.viva.play.utils.web.utils

import com.viva.play.App
import java.io.File

/**
 * @author 李雄厚
 *
 *
 */
object CacheUtils {

    /**
     * 获取系统默认缓存文件夹
     * 优先返回SD卡中的缓存文件夹
     */
    fun getCacheDir(): String? {
        var cacheFile: File? = null
        if (FileUtils.isSDCardAlive()) {
            cacheFile = App.instance.externalCacheDir
        }
        if (cacheFile == null) {
            cacheFile = App.instance.cacheDir
        }
        return cacheFile?.absolutePath
    }

    fun getFilesDir(): String? {
        val cacheFile = App.instance.filesDir
        return cacheFile.absolutePath
    }

    /**
     * 获取系统默认缓存文件夹内的缓存大小
     */
    fun getTotalCacheSize(): String {
        var cacheSize = FileUtils.getSize(App.instance.cacheDir)
        if (FileUtils.isSDCardAlive()) {
            cacheSize += FileUtils.getSize(App.instance.externalCacheDir)
        }
        return FileUtils.formatSize(cacheSize.toDouble())
    }

    /**
     * 清除系统默认缓存文件夹内的缓存
     */
    fun clearAllCache() {
        FileUtils.delete(App.instance.cacheDir)
        if (FileUtils.isSDCardAlive()) {
            FileUtils.delete(App.instance.externalCacheDir)
        }
    }
}