package com.viva.play.utils.web.cookie.persistence

import okhttp3.Cookie

/**
 * @author 李雄厚
 *
 * A CookiePersistor handles the persistent cookie storage.
 */
interface CookiePersistor {

    fun loadAll(): List<Cookie>

    /**
     * Persist all cookies, existing cookies will be overwritten.
     *
     * @param cookies cookies persist
     */
    fun saveAll(cookies: Collection<Cookie>)

    /**
     * Removes indicated cookies from persistence.
     *
     * @param cookies cookies to remove from persistence
     */
    fun removeAll(cookies: Collection<Cookie>)

    /**
     * Clear all cookies from persistence.
     */
    fun clear()
}