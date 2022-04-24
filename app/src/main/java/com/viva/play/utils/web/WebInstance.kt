package com.viva.play.utils.web

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.MutableContextWrapper
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.tencent.smtt.export.external.interfaces.IX5WebSettings
import com.tencent.smtt.sdk.CookieManager
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.viva.play.App
import com.viva.play.R
import com.viva.play.views.X5WebView
import java.lang.Exception
import java.util.ArrayList




/**
 * @author 李雄厚
 *
 *
 */
class WebInstance(private val application: Application) {

    companion object {
        @Volatile
        private var sInstance: WebInstance? = null
        fun getInstance(context: Context): WebInstance {
            return sInstance ?: synchronized(this) {
                sInstance ?: WebInstance(context.applicationContext as Application).also {
                    sInstance = it
                }
            }
        }
    }

    private val mCache = ArrayList<WebView>(1)

    init {
        Looper.myQueue().addIdleHandler {
            val webView = create()
            mCache.add(webView)
            false
        }

    }

    @SuppressLint("SetJavaScriptEnabled")
    fun obtain(): WebView {
        if (mCache.isEmpty()) {
            return create()
        }
        val webView = mCache.removeAt(0)
        val webSetting = webView.settings
        webSetting.javaScriptEnabled = true
        webView.clearHistory()
        webView.resumeTimers()
        return webView
    }

    fun recycle(webView: WebView) {
        val parent = webView.parent
        if (parent != null) {
            (parent as ViewGroup).removeView(webView)
        }
        try {
            webView.stopLoading()
            var step = 0
            while (webView.canGoBackOrForward(step - 1)) step--
            webView.goBackOrForward(step)
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            webView.clearHistory()
            val webSetting = webView.settings
            webSetting.javaScriptEnabled = false
            webView.pauseTimers()
            webView.webChromeClient = null
            webView.webViewClient = null
        } catch (ignore: Exception) {
        } finally {
            if (!mCache.contains(webView)) {
                mCache.add(webView)
            }
        }
    }

    fun destroy(webView: WebView) {
        recycle(webView)
        try {
            webView.removeAllViews()
            webView.destroy()
        } catch (ignore: Exception) {
        } finally {
            mCache.remove(webView)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun create(): WebView {
        val webView: WebView = X5WebView(MutableContextWrapper(application))
        webView.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        webView.setBackgroundColor(0)
        webView.background.alpha = 0
        webView.overScrollMode = WebView.OVER_SCROLL_NEVER
        webView.view.overScrollMode = View.OVER_SCROLL_NEVER

        val webSetting = webView.settings
        webSetting.javaScriptEnabled = true
        webSetting.javaScriptCanOpenWindowsAutomatically = false
        webSetting.allowFileAccess = true
        webSetting.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        webSetting.setSupportZoom(false)
        webSetting.builtInZoomControls = false
        webSetting.useWideViewPort = true
        webSetting.loadWithOverviewMode = true
        webSetting.setAppCacheEnabled(true)
        webSetting.domStorageEnabled = true
        webSetting.setGeolocationEnabled(true)
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE)
        webSetting.pluginState = WebSettings.PluginState.ON_DEMAND
        webSetting.cacheMode = WebSettings.LOAD_DEFAULT
        webSetting.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        val ext = webView.settingsExtension
        ext?.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        return webView
    }
}