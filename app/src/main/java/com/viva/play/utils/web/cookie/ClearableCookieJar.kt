package com.viva.play.utils.web.cookie

import okhttp3.CookieJar

/**
 * @author 李雄厚
 *
 * This interface extends [CookieJar] and adds methods to clear the cookies.
 */
interface ClearableCookieJar : CookieJar {

    /**
     *  Clear all the session cookies while maintaining the persisted ones.
     */
    fun clearSession()

    /**
     *  Clear all the cookies from persistence and from the cache.
     */
    fun clear()
}