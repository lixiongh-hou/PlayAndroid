package com.viva.play.utils.web.cookie.cache

import okhttp3.Cookie

/**
 * @author 李雄厚
 *
 *
 */
class SetCookieCache : CookieCache {

    private val cookies = hashSetOf<IdentifiableCookie>()

    override fun addAll(cookies: Collection<Cookie>) {
        for (cookie in IdentifiableCookie.decorateAll(cookies)) {
            this.cookies.remove(cookie)
            this.cookies.add(cookie)
        }
    }

    override fun clear() {
        cookies.clear()
    }

    override fun iterator(): Iterator<Cookie> {
        return SetCookieCacheIterator()
    }

    private inner class SetCookieCacheIterator : Iterator<Cookie> {

        private val iterator = cookies.iterator()

        override fun hasNext(): Boolean {
            return iterator.hasNext()
        }

        override fun next(): Cookie {
            return iterator.next().cookie
        }

        fun remove() {
            iterator.remove()
        }
    }
}