package com.viva.play.utils

import android.os.Bundle
import com.tencent.smtt.sdk.WebView
import com.tencent.sonic.sdk.SonicSessionClient
import java.util.HashMap

/**
 * @author 李雄厚
 *
 *
 */
class WebSessionClientImpl : SonicSessionClient() {
    private var webView: WebView? = null

    fun bindWebView(webView: WebView?) {
        this.webView = webView
    }

    override fun loadUrl(url: String?, extraData: Bundle?) {
        webView?.loadUrl(url)
    }

    override fun loadDataWithBaseUrl(
        baseUrl: String?,
        data: String?,
        mimeType: String?,
        encoding: String?,
        historyUrl: String?
    ) {
        webView?.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl)
    }

    override fun loadDataWithBaseUrlAndHeader(
        baseUrl: String?,
        data: String?,
        mimeType: String?,
        encoding: String?,
        historyUrl: String?,
        headers: HashMap<String, String>?
    ) {
        if (headers?.isEmpty() == true) {
            webView?.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl)
        } else {
            webView?.loadUrl(baseUrl, headers)
        }
    }
}