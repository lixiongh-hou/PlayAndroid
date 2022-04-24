package com.viva.play.utils.web.cookie

import com.viva.play.utils.web.cookie.cache.CookieCache
import com.viva.play.utils.web.cookie.persistence.CookiePersistor
import okhttp3.Cookie
import okhttp3.HttpUrl

/**
 * @author 李雄厚
 *
 *
 */
class PersistentCookieJar(
    private val cache: CookieCache,
    private val persistor: CookiePersistor
) : ClearableCookieJar {

    init {

        cache.addAll(persistor.loadAll())
    }

    @Synchronized
    override fun clearSession() {
        cache.clear()
        cache.addAll(persistor.loadAll())
    }

    @Synchronized
    override fun clear() {
        cache.clear()
        persistor.clear()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookiesToRemove = mutableListOf<Cookie>()
        val validCookies = mutableListOf<Cookie>()
        val it = cache.iterator()
        while (it.hasNext()) {
            val currentCookie = it.next()
            if (isCookieExpired(currentCookie)) {
                cookiesToRemove.add(currentCookie)
                it as MutableCollection<*>
                it.clear()
            } else if (currentCookie.matches(url)) {
                validCookies.add(currentCookie)
            }
        }

        persistor.removeAll(cookiesToRemove)

        return validCookies
    }

    private fun isCookieExpired(cookie: Cookie): Boolean {
        return cookie.expiresAt < System.currentTimeMillis()
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cache.addAll(cookies)
        persistor.saveAll(filterPersistentCookies(cookies))
    }

    private fun filterPersistentCookies(cookies: List<Cookie>): List<Cookie> {
        val persistentCookies = mutableListOf<Cookie>()
        for (cookie in cookies) {
            if (cookie.persistent) {
                persistentCookies.add(cookie)
            }
        }
        return persistentCookies
    }
}