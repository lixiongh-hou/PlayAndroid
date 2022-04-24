package com.viva.play.utils.web

import android.animation.Animator
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.annotation.FloatRange
import androidx.annotation.RequiresApi
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.export.external.interfaces.WebResourceResponse
import com.tencent.smtt.sdk.*
import com.viva.play.R
import com.viva.play.utils.SettingUtils
import com.viva.play.utils.ToastUtil.toast
import com.viva.play.utils.dpToPx
import com.viva.play.utils.getThemeColor
import com.viva.play.utils.web.utils.PersistentCookieUtils
import me.zhanghai.android.materialprogressbar.MaterialProgressBar
import org.json.JSONException
import org.json.JSONObject
import per.goweii.anylayer.DecorLayer
import per.goweii.anylayer.Layer.AnimatorCreator
import java.lang.Exception
import java.util.ArrayList
import com.tencent.sonic.sdk.SonicEngine

import com.tencent.sonic.sdk.SonicSessionConfig
import com.viva.play.utils.WebSessionClientImpl
import com.tencent.sonic.sdk.SonicSession


/**
 * @author 李雄厚
 *
 *
 */
class WebHolder(
    activity: Activity,
    mUrl: String,
    container: WebContainer,
    progressBar: ProgressBar?
) {

    private var mOnPageLoadCallback: OnPageLoadCallback? = null
    private var mOnPageProgressCallback: OnPageProgressCallback? = null

    private var mActivity: Activity
    private var mWebContainer: WebContainer
    private var mWebView: WebView
    private var mProgressBar: ProgressBar
    private var mUserAgentString: String

    private var allowOpenOtherApp = true
    private var allowOpenDownload = true
    private var allowRedirect = true

    private var jsInjector: JsInjector

    private var useInstanceCache = true
    private var isProgressShown = false
    private var isPageScrollEnd = false

    private var client: WebSessionClientImpl? = null
    private var sonicSession: SonicSession? = null

    companion object {
        private val TAG = WebHolder::class.java.simpleName

        fun with(
            activity: Activity,
            mUrl: String,
            container: WebContainer,
            progressBar: ProgressBar? = null
        ): WebHolder = WebHolder(activity, mUrl, container, progressBar)

        fun syncCookiesForWanAndroid(url: String?) {
            if (url.isNullOrEmpty()) {
                return
            }
            val host = Uri.parse(url).host
            if (host != "www.wanandroid.com") {
                return
            }
            val cookies = PersistentCookieUtils.loadForUrl(url)
            if (cookies.isNullOrEmpty()) {
                return
            }
            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)
            cookieManager.removeSessionCookies(null)

            for (cookie in cookies) {
                val name: String = cookie.name
                cookieManager.setCookie(url, name + "=" + cookie.value)
            }
            cookieManager.flush()
        }
    }

    init {
        activity.window.setFormat(PixelFormat.TRANSLUCENT)
        mActivity = activity
        mWebContainer = container
        val color = activity.getThemeColor(R.attr.colorSurface)
        mWebContainer.setBackgroundColor(color)
        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            QbSdk.forceSysWebView()
        } else {
            QbSdk.unForceSysWebView()
        }
        mWebView = if (useInstanceCache) {
            WebInstance.getInstance(mActivity).obtain()
        } else {
            WebInstance.getInstance(mActivity).create()
        }

        //设置预加载
        val config = SonicSessionConfig.Builder().build()
        SonicEngine.getInstance().preCreateSession(mUrl, config)
        sonicSession = SonicEngine.getInstance().createSession(mUrl, config)
        if (null != sonicSession) {
            sonicSession?.bindClient(WebSessionClientImpl().also { client = it })
        }

        container.addView(
            mWebView, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        if (progressBar == null) {
            mProgressBar = LayoutInflater.from(activity)
                .inflate(R.layout.basic_progress_bar, container, false) as MaterialProgressBar
            container.addView(
                mProgressBar, FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    2.dpToPx
                )
            )
        } else {
            mProgressBar = progressBar
        }
        mProgressBar.max = 100
        mWebView.webChromeClient = WanWebChromeClient()
        mWebView.webViewClient = WanWebViewClient()
        mWebView.setDownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
            Log.i(TAG, "onDownloadStart:url=$url")
            Log.i(TAG, "onDownloadStart:userAgent=$userAgent")
            Log.i(TAG, "onDownloadStart:contentDisposition=$contentDisposition")
            Log.i(TAG, "onDownloadStart:mimeType=$mimeType")
            Log.i(TAG, "onDownloadStart:contentLength=$contentLength")
            if (!allowOpenDownload) return@setDownloadListener
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.addCategory(Intent.CATEGORY_BROWSABLE)
                intent.data = Uri.parse(url)
                mWebView.context.startActivity(intent)
            } catch (ignore: Exception) {
            }
        }

        mWebView.setOnLongClickListener {
            val hitTestResult = mWebView.hitTestResult
            val result = HitResult(hitTestResult)
            return@setOnLongClickListener when (result.getType()) {
                HitResult.Type.IMAGE_TYPE, HitResult.Type.IMAGE_ANCHOR_TYPE, HitResult.Type.SRC_IMAGE_ANCHOR_TYPE -> {
                    "显示图片${result.getResult()}".toast()
                    true
                }
                else -> false
            }
        }

        mWebView.setOnScrollChangeListener(View.OnScrollChangeListener { _, _, scrollY, _, _ ->
            if (isProgressShown) return@OnScrollChangeListener
            val percent = getPercent()
            pageScrolled?.invoke(percent)
            if (!isPageScrollEnd && percent >= 0.95) {
                isPageScrollEnd = true
                pageScrollEnd?.invoke()
            }
        })

        val webSetting = mWebView.settings
        mUserAgentString = webSetting.userAgentString
        val isAppDarkMode: Boolean = SettingUtils.darkTheme
        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            val v = mWebView.view
            if (v is android.webkit.WebView) {
                if (isAppDarkMode) {
                    WebSettingsCompat.setForceDark(v.settings, WebSettingsCompat.FORCE_DARK_ON)
                } else {
                    WebSettingsCompat.setForceDark(v.settings, WebSettingsCompat.FORCE_DARK_OFF)
                }
            }
        } else {
            mWebView.setDayOrNight(!isAppDarkMode)
        }
        jsInjector = JsInjector(Looper.getMainLooper(), mWebView)
        jsInjector.attach()

    }

    fun loadUrl(url: String?): WebHolder {
        if (client != null) {
            client?.bindWebView(mWebView)
            client?.clientReady()
        } else {
            mWebView.loadUrl(url)
        }
        return this
    }

    fun getUrl(): String {
        val url = mWebView.url
        return url ?: ""
    }

    @FloatRange(from = 0.0, to = 1.0)
    fun getPercent(): Float {
        if (isProgressShown) return 0f
        val contentHeight = mWebView.contentHeight * mWebView.scale
        if (contentHeight <= 0) return 0f
        val webViewScrollY = mWebView.webScrollY.toFloat()
        val webViewHeight = mWebView.height.toFloat()
        var percent = (webViewScrollY + webViewHeight) / contentHeight
        percent = 0f.coerceAtLeast(percent)
        percent = 1f.coerceAtMost(percent)
        return percent
    }


    fun getShareInfo(callback: ((url: String, covers: List<String>, title: String, desc: String) -> Unit)? = null) {
        val js = "javascript:(" +
                "function getShareInfo() {" +
                "  var map = {};" +
                "  map[\"title\"] = document.title;" +
                "  map[\"desc\"] = document.querySelector('meta[name=\"description\"]').getAttribute('content');" +
                "  var imgElements = document.getElementsByTagName(\"img\");" +
                "  var imgs = [];" +
                "  for(var i = 0 ; i < imgElements.length; i++){" +
                "    var imgEle = imgElements[i];" +
                "    var w = imgEle.naturalWidth;" +
                "    var h = imgEle.naturalHeight;" +
                "    if(w > 200 && h > 100){" +
                "      imgs.push(imgEle.src);" +
                "    }" +
                "  }" +
                "  map[\"imgs\"] = imgs;" +
                "  return map;" +
                "}" +
                ")()"
        mWebView.evaluateJavascript(js) { s ->
            var title = ""
            var desc = ""
            val imgs: MutableList<String> =
                ArrayList()
            try {
                val jsonObject = JSONObject(s)
                title = jsonObject.optString("title")
                desc = jsonObject.optString("desc")
                val imgArr = jsonObject.optJSONArray("imgs")
                if (imgArr != null) {
                    for (i in 0 until imgArr.length()) {
                        val img = imgArr.optString(i)
                        if (!TextUtils.isEmpty(img)) {
                            if (!imgs.contains(img)) {
                                imgs.add(img)
                            }
                        }
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            if (TextUtils.isEmpty(title)) {
                title = getTitle()
            }
            if (TextUtils.isEmpty(desc)) {
                desc = getUrl()
            }
            callback?.invoke(getUrl(), imgs, title, desc)
        }
    }

    fun getTitle(): String {
        val title = mWebView.title
        return title ?: ""
    }

    fun getUserAgent(): String {
        return mUserAgentString
    }

    fun canGoBack(): Boolean {
        return mWebView.canGoBack()
    }

    fun canGoForward(): Boolean {
        return mWebView.canGoForward()
    }

    fun canGoBackOrForward(steps: Int): Boolean {
        return mWebView.canGoBackOrForward(steps)
    }

    fun goBack() {
        mWebView.goBack()
    }

    fun goForward() {
        mWebView.goForward()
    }

    fun goBackOrForward(steps: Int) {
        mWebView.goBackOrForward(steps)
    }

    fun reload() {
        mWebView.reload()
    }

    fun stopLoading() {
        mWebView.stopLoading()
    }

    fun goTop() {
        val js = "javascript:(" +
                "function(){\n" +
                "  var timer = null;\n" +
                "  cancelAnimationFrame(timer);\n" +
                "  var startTime = +new Date();     \n" +
                "  var b = document.body.scrollTop || document.documentElement.scrollTop;\n" +
                "  var d = 500;\n" +
                "  var c = b;\n" +
                "  var timer = requestAnimationFrame(function func(){\n" +
                "    var t = d - Math.max(0,startTime - (+new Date()) + d);\n" +
                "    document.documentElement.scrollTop = document.body.scrollTop = t * (-c) / d + b;\n" +
                "    timer = requestAnimationFrame(func);\n" +
                "    if(t == d){\n" +
                "      cancelAnimationFrame(timer);\n" +
                "    }\n" +
                "  });\n" +
                "}" +
                ")()"
        mWebView.evaluateJavascript(js, null)
    }

    fun scrollToKeywords(keywordList: List<String?>?) {
        val keywords = TextUtils.join(",", keywordList!!)
        val js = "javascript:(\n" +
                "    function(){\n" +
                "        var keywords = '" + keywords + "'.split(\",\");\n" +
                "        var pElements = document.getElementsByTagName('p');\n" +
                "        for(var i = 0; i < pElements.length; i++) {\n" +
                "            var pEle = pElements[i];\n" +
                "            var pText = pEle.innerHTML;\n" +
                "            var match = true;\n" +
                "            for (var j = 0; j < keywords.length; j++) {\n" +
                "                var index = pText.indexOf(keywords[j]);\n" +
                "                if (index === -1) {\n" +
                "                    match = false;\n" +
                "                    break;\n" +
                "                }\n" +
                "            }\n" +
                "            if (match) {\n" +
                "                pEle.scrollIntoView();\n" +
                "                break;\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                ")()";
        mWebView.evaluateJavascript(js, null)
    }

    fun onResume() {
        mWebView.resumeTimers()
        mWebView.onResume()
    }

    fun onPause() {
        mWebView.pauseTimers()
        mWebView.onPause()
    }

    fun onDestroy(destroyOrRecycle: Boolean) {
        jsInjector.detach()
        mProgressBar.clearAnimation()
        if (null != sonicSession) {
            sonicSession?.destroy()
            sonicSession = null
        }
        if (useInstanceCache) {
            if (destroyOrRecycle) {
                WebInstance.getInstance(mActivity)
                    .destroy(mWebView)
            } else {
                WebInstance.getInstance(mActivity)
                    .recycle(mWebView)
            }
        } else {
            WebInstance.getInstance(mActivity).destroy(mWebView)
        }
    }

    fun handleKeyEvent(keyCode: Int): Boolean {
        if (keyCode != KeyEvent.KEYCODE_BACK) {
            return false
        }
        if (mWebView.canGoBack()) {
            mWebView.goBack()
            return true
        }
        return false
    }

    fun setLoadCacheElseNetwork(loadCacheElseNetwork: Boolean): WebHolder {
        val webSetting = mWebView.settings
        if (loadCacheElseNetwork) {
            webSetting.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        } else {
            webSetting.cacheMode = WebSettings.LOAD_DEFAULT
        }
        return this
    }

    fun setUseInstanceCache(useInstanceCache: Boolean): WebHolder {
        this.useInstanceCache = useInstanceCache
        return this
    }

    fun setAllowOpenOtherApp(allowOpenOtherApp: Boolean): WebHolder {
        this.allowOpenOtherApp = allowOpenOtherApp
        return this
    }

    fun setAllowOpenDownload(allowOpenDownload: Boolean): WebHolder {
        this.allowOpenDownload = allowOpenDownload
        return this
    }

    fun setAllowRedirect(allowRedirect: Boolean): WebHolder {
        this.allowRedirect = allowRedirect
        return this
    }

    fun setOnPageTitleCallback(onPageTitleCallback: (title: String) -> Unit): WebHolder {
        receivedTitle = onPageTitleCallback
        return this
    }

    fun setOnPageScrollEndListener(onPageScrollEndListener: () -> Unit): WebHolder {
        pageScrollEnd = onPageScrollEndListener
        return this
    }

    fun setOnPageScrollChangeListener(pageScrolled: (percent: Float) -> Unit): WebHolder {
        this.pageScrolled = pageScrolled
        return this
    }

    fun setOnPageLoadCallback(onPageLoadCallback: OnPageLoadCallback): WebHolder {
        mOnPageLoadCallback = onPageLoadCallback
        return this
    }

    fun setOnPageProgressCallback(onPageProgressCallback: OnPageProgressCallback): WebHolder {
        mOnPageProgressCallback = onPageProgressCallback
        return this
    }

    fun setOnHistoryUpdateCallback(onHistoryUpdateCallback: (isReload: Boolean) -> Unit): WebHolder {
        historyUpdate = onHistoryUpdateCallback
        return this
    }

    fun setOverrideUrlInterceptor(overrideUrlInterceptor: (url: String) -> Boolean): WebHolder {
        overrideUrl = overrideUrlInterceptor
        return this
    }

    fun setInterceptUrlInterceptor(interceptUrlInterceptor: (reqUri: Uri, reqHeaders: Map<String, String>?, reqMethod: String?) -> WebResourceResponse?): WebHolder {
        interceptUrl = interceptUrlInterceptor
        return this
    }

    inner class WanWebChromeClient : WebChromeClient() {
        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
            receivedTitle?.invoke(title ?: "")
        }

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            isPageScrollEnd = false
            jsInjector.onProgressChanged(newProgress)
            if (newProgress < 30) {
                if (!isProgressShown) {
                    isProgressShown = true
                    onShowProgress()
                }
                onProgressChanged(newProgress)
            } else if (newProgress > 80) {
                onProgressChanged(newProgress)
                if (isProgressShown) {
                    isProgressShown = false
                    onHideProgress()
                }
            } else {
                onProgressChanged(newProgress)
            }
        }

        private fun onShowProgress() {
            showProgress()
            mOnPageProgressCallback?.onShowProgress()

        }

        private fun onProgressChanged(progress: Int) {
            setProgress(progress)
            mOnPageProgressCallback?.onProgressChanged(progress)

        }

        private fun onHideProgress() {
            hideProgress()
            mOnPageProgressCallback?.onHideProgress()
        }

        private fun setProgress(progress: Int) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mProgressBar.setProgress(progress, true)
            } else {
                mProgressBar.progress = progress
            }
        }

        private fun showProgress() {
            mProgressBar.animate()
                .alpha(1f)
                .setDuration(200)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                        mProgressBar.visibility = View.VISIBLE
                        setProgress(0)
                    }

                    override fun onAnimationEnd(animation: Animator) {}
                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                }).start()
        }

        private fun hideProgress() {
            mProgressBar.animate()
                .alpha(0F)
                .setDuration(200)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {}
                    override fun onAnimationEnd(animation: Animator) {
                        setProgress(100)
                        mProgressBar.visibility = View.GONE
                    }

                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                }).start()
        }

        private var mCustomViewLayer: DecorLayer? = null
        private var mCustomViewCallback: IX5WebChromeClient.CustomViewCallback? = null
        private var mOldActivityOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        override fun onShowCustomView(
            view: View,
            customViewCallback: IX5WebChromeClient.CustomViewCallback?
        ) {
            if (mCustomViewLayer != null) {
                mCustomViewLayer?.dismiss()
                mCustomViewLayer = null
            }

            if (mCustomViewCallback != null) {
                mCustomViewCallback?.onCustomViewHidden()
                mCustomViewCallback = null
            }

            mCustomViewCallback = customViewCallback
            mCustomViewLayer = DecorLayer(mActivity)
            mCustomViewLayer?.level(Int.MAX_VALUE)
            mCustomViewLayer!!.animator(object : AnimatorCreator {
                override fun createInAnimator(target: View): Animator? {
                    return null
                }

                override fun createOutAnimator(target: View): Animator? {
                    return null
                }
            })
            view.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            mCustomViewLayer?.child(view)
            mCustomViewLayer?.show()
            mOldActivityOrientation = mActivity.requestedOrientation
            mActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            mActivity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        }

        override fun onHideCustomView() {
            if (mCustomViewLayer != null) {
                mCustomViewLayer?.dismiss()
                mCustomViewLayer = null
            }
            if (mCustomViewCallback != null) {
                mCustomViewCallback?.onCustomViewHidden()
                mCustomViewCallback = null
            }
            mActivity.requestedOrientation = mOldActivityOrientation
            mActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    inner class WanWebViewClient : WebViewClient() {
        private fun shouldInterceptRequest(
            reqUri: Uri,
            reqHeaders: Map<String, String>?,
            reqMethod: String?
        ): WebResourceResponse? {
            val url = reqUri.toString()
            Log.d(TAG, "shouldInterceptRequest:url=$url")
            Log.d(TAG, "shouldInterceptRequest:headers=$reqHeaders")
            Log.d(TAG, "shouldInterceptRequest:method=$reqMethod")
            syncCookiesForWanAndroid(url)
            return if (interceptUrl == null) null else interceptUrl?.invoke(
                reqUri,
                reqHeaders,
                reqMethod
            )
        }

        private fun shouldOverrideUrlLoading(view: WebView, uri: Uri): Boolean {
            Log.i(TAG, "shouldOverrideUrlLoading=$uri")
            val url = view.url
            val originalUrl = view.originalUrl
            val hit = view.hitTestResult
            if (hit.type == WebView.HitTestResult.UNKNOWN_TYPE || TextUtils.isEmpty(hit.extra)) {
                Log.i(TAG, "重定向:url=$url")
                Log.i(TAG, "重定向:originalUrl=$originalUrl")
                if (!allowRedirect) {
                    if (!TextUtils.isEmpty(originalUrl) && (originalUrl.startsWith("http://") || originalUrl.startsWith(
                            "https://"
                        ))
                    ) {
                        return true
                    }
                }
            }
            val scheme = uri.scheme
            if (!(TextUtils.equals(scheme, "http") || TextUtils.equals(scheme, "https"))) {
                if (allowOpenOtherApp) {
                    try {
                        val context = view.context
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                return true
            }
            if (overrideUrl != null) {
                if (overrideUrl?.invoke(uri.toString()) == true) {
                    return true
                }
            }
            //广告拦截
            when (SettingUtils.urlInterceptType) {
                HostInterceptUtils.TYPE_ONLY_WHITE -> if (!HostInterceptUtils.isWhiteHost(uri.host)) {
                    return true
                }
                HostInterceptUtils.TYPE_INTERCEPT_BLACK -> if (HostInterceptUtils.isBlackHost(uri.host)) {
                    return true
                }
                HostInterceptUtils.TYPE_NOTHING -> {
                }
                else -> {
                }
            }
            return false
        }

        override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
            val reqUri = Uri.parse(url)
            val response = shouldInterceptRequest(reqUri, null, null)
            return response
                ?: if (sonicSession != null) {
                    sonicSession?.sessionClient?.requestResource(url) as WebResourceResponse
                } else null
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            val reqUri = request!!.url ?: return super.shouldInterceptRequest(view, request)
            val reqHeaders = request.requestHeaders
            val reqMethod = request.method
            val response = shouldInterceptRequest(reqUri, reqHeaders, reqMethod)
            return response ?: super.shouldInterceptRequest(view, request.url.toString())
        }

        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest,
            bundle: Bundle?
        ): WebResourceResponse? {
            val reqUri = request.url ?: return super.shouldInterceptRequest(view, request, bundle)
            val reqHeaders = request.requestHeaders
            val reqMethod = request.method
            val response = shouldInterceptRequest(reqUri, reqHeaders, reqMethod)
            return response ?: super.shouldInterceptRequest(view, request, bundle)
        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            return shouldOverrideUrlLoading(view!!, Uri.parse(url))
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest
        ): Boolean {
            return shouldOverrideUrlLoading(view!!, request.url)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            jsInjector.onPageStarted()
            super.onPageStarted(view, url, favicon)
            receivedTitle?.invoke(getUrl())
            mOnPageLoadCallback?.onPageStarted()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            if (sonicSession != null) {
                sonicSession?.sessionClient?.pageFinish(url)
            }
            receivedTitle?.invoke(getTitle())
            mOnPageLoadCallback?.onPageFinished()

        }

        override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
            super.doUpdateVisitedHistory(view, url, isReload)
            historyUpdate?.invoke(isReload)
        }
    }


    private var pageScrollEnd: (() -> Unit)? = null
    private var pageScrolled: ((percent: Float) -> Unit)? = null
    private val hitTestResult: ((result: HitResult) -> Unit)? = null
    private var receivedTitle: ((title: String) -> Unit)? = null

    interface OnPageLoadCallback {
        fun onPageStarted()
        fun onPageFinished()
    }

    interface OnPageProgressCallback {
        fun onShowProgress()
        fun onProgressChanged(progress: Int)
        fun onHideProgress()
    }

    private var historyUpdate: ((isReload: Boolean) -> Unit)? = null
    private var overrideUrl: ((url: String) -> Boolean)? = null
    private var interceptUrl: (
        (reqUri: Uri, reqHeaders: Map<String, String>?, reqMethod: String?) -> WebResourceResponse?)? =
        null
}
