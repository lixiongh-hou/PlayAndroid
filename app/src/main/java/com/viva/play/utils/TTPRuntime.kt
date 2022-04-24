package com.viva.play.utils

import android.content.Context
import com.tencent.smtt.sdk.CookieManager
import com.tencent.sonic.sdk.SonicRuntime
import com.tencent.sonic.sdk.SonicSessionClient
import java.io.InputStream

/**
 * @author 李雄厚
 *
 *
 */
class TTPRuntime(context: Context) : SonicRuntime(context) {
    override fun log(tag: String?, level: Int, message: String?) {
    }

    override fun getCookie(url: String?): String? {
        val cookieManager = CookieManager.getInstance()
        return cookieManager.getCookie(url)
    }

    override fun setCookie(url: String?, cookies: MutableList<String>?): Boolean {
        if (!url.isNullOrEmpty() && cookies != null && cookies.size > 0) {
            val cookieManager = CookieManager.getInstance()
            for (cookie in cookies) {
                cookieManager.setCookie(url, cookie)
            }
            return true
        }
        return false
    }

    override fun getUserAgent(): String {
        return "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Mobile Safari/537.36"
    }

    override fun getCurrentUserAccount(): String {
        return ""
    }

    override fun isSonicUrl(url: String?): Boolean {
        return true
    }

    override fun createWebResourceResponse(
        mimeType: String?,
        encoding: String?,
        data: InputStream?,
        headers: MutableMap<String, String>?
    ): Any? {
        return null
    }

    override fun isNetworkValid(): Boolean {
        return true
    }

    override fun showToast(text: CharSequence?, duration: Int) {
    }

    override fun postTaskToThread(task: Runnable?, delayMillis: Long) {
        val thread = Thread(task, "SonicThread")
        thread.start()
    }

    override fun notifyError(client: SonicSessionClient?, url: String?, errorCode: Int) {
    }
}