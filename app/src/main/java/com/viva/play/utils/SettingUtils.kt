package com.viva.play.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.viva.play.service.FileKey
import com.viva.play.utils.web.HostEntity
import com.viva.play.utils.web.HostInterceptUtils
import java.lang.Exception

/**
 * @author 李雄厚
 *
 *
 */

object SettingUtils {

    /**
     * 设置暗黑主题
     */
    var darkTheme by Preference(FileKey.KEY_DARK_THEME, false)

    /**
     * 网络拦截规则
     */
    var urlInterceptType by Preference(FileKey.URL_INTERCEPT_TYPE, HostInterceptUtils.TYPE_NOTHING)

    /**
     * 网络拦截白名单
     */
    private var hostWhite by Preference(FileKey.HOST_WHITE, "")

    /**
     * 网络拦截黑名单
     */
    private var hostBlack by Preference(FileKey.HOST_BLACK, "")


    fun setHostWhiteIntercept(host: List<HostEntity>) {
        val json = Gson().toJson(host)
        hostWhite = json
    }

    fun setHostBlackIntercept(host: List<HostEntity>) {
        val json = Gson().toJson(host)
        hostBlack = json
    }

    fun getHostWhiteIntercept(): List<HostEntity> {
        return try {
            Gson().fromJson(hostWhite, object : TypeToken<List<HostEntity>>() {}.type)
        } catch (e: Exception) {
            HostInterceptUtils.WHITE_HOST.map {
                HostEntity(it)
            }
        }
    }

    fun getHostBlackIntercept(): List<HostEntity> {
        return try {
            Gson().fromJson(hostBlack, object : TypeToken<List<HostEntity>>() {}.type)
        } catch (e: Exception) {
            HostInterceptUtils.BLACK_HOST.map {
                HostEntity(it)
            }
        }
    }
}