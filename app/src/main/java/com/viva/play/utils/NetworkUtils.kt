package com.viva.play.utils

import android.content.Context
import android.net.NetworkInfo

import android.net.ConnectivityManager
import com.viva.play.App


/**
 * @author 李雄厚
 *
 *
 */
object NetworkUtils {
    /**
     * 检测当的网络（WLAN、3G/2G）状态
     * @return true 表示网络可用
     */
    fun isNetworkAvailable(): Boolean {
        val connectivity = App.instance
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivity.activeNetworkInfo
        if (info != null && info.isConnected) {
            // 当前网络是连接的
            if (info.state == NetworkInfo.State.CONNECTED) {
                // 当前所连接的网络可用
                return true
            }
        }
        return false
    }
}

