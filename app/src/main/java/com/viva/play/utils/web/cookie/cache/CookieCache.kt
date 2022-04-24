package com.viva.play.utils.web.cookie.cache

import okhttp3.Cookie

/**
 * @author 李雄厚
 *
 * A CookieCache handles the volatile cookie session storage.
 */
interface CookieCache : Iterable<Cookie> {

    /**
     * Add all the new cookies to the session, existing cookies will be overwritten.
     *
     * @param cookies
     */
    fun addAll(cookies: Collection<Cookie>)

    /**
     * Clear all the cookies from the session.
     */
    fun clear()
}