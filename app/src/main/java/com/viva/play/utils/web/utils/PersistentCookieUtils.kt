package com.viva.play.utils.web.utils

import android.content.Context
import com.viva.play.utils.web.cookie.PersistentCookieJar
import com.viva.play.utils.web.cookie.cache.SetCookieCache
import com.viva.play.utils.web.cookie.persistence.SharedPrefsCookiePersistor
import okhttp3.Cookie
import okhttp3.HttpUrl.Companion.toHttpUrl

object PersistentCookieUtils {
    lateinit var cookieJar: PersistentCookieJar

    fun init(context: Context) {
        cookieJar = PersistentCookieJar(
            SetCookieCache(),
                SharedPrefsCookiePersistor(context.applicationContext)
        )
    }

    fun loadForUrl(url: String): List<Cookie> {
        if (url.isBlank()) {
            return arrayListOf()
        }
        if (!this::cookieJar.isInitialized) {
            return arrayListOf()
        }
        return cookieJar.loadForRequest(url.toHttpUrl())
    }
}