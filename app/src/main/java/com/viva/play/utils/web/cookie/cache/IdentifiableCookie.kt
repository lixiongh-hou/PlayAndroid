package com.viva.play.utils.web.cookie.cache

import okhttp3.Cookie

/**
 * @author 李雄厚
 *
 *
 */
class IdentifiableCookie(val cookie: Cookie) {

    companion object {
        fun decorateAll(cookies: Collection<Cookie>): List<IdentifiableCookie> {
            val identifiableCookies = ArrayList<IdentifiableCookie>(cookies.size)
            for (cookie in cookies) {
                identifiableCookies.add(IdentifiableCookie(cookie))
            }
            return identifiableCookies
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is IdentifiableCookie) return false
        return other.cookie.name == cookie.name && other.cookie.domain == cookie.domain && other.cookie.path == cookie.path && other.cookie.secure == cookie.secure && other.cookie.hostOnly == cookie.hostOnly
    }

    override fun hashCode(): Int {
        var hash = 17
        hash = 31 * hash + cookie.name.hashCode()
        hash = 31 * hash + cookie.domain.hashCode()
        hash = 31 * hash + cookie.path.hashCode()
        hash = 31 * hash + if (cookie.secure) 0 else 1
        hash = 31 * hash + if (cookie.hostOnly) 0 else 1
        return hash
    }

}