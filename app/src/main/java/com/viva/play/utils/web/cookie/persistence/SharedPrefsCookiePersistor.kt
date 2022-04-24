package com.viva.play.utils.web.cookie.persistence

import android.content.Context
import okhttp3.Cookie
import java.util.*

/**
 * @author 李雄厚
 *
 *
 */
class SharedPrefsCookiePersistor(private val context: Context) : CookiePersistor {

    private val sharedPreferences =
        context.getSharedPreferences("CookiePersistence", Context.MODE_PRIVATE)

    override fun loadAll(): List<Cookie> {
        val cookies = ArrayList<Cookie>(sharedPreferences.all.size)
        for (entry in sharedPreferences.all.entries) {
            val serializedCookie = entry.value as String
            val cookie = SerializableCookie().decode(serializedCookie)
            if (cookie != null) {
                cookies.add(cookie)
            }
        }
        return cookies
    }

    override fun saveAll(cookies: Collection<Cookie>) {
        val editor = sharedPreferences.edit()
        for (cookie in cookies) {
            editor.putString(createCookieKey(cookie), SerializableCookie().encode(cookie))
        }
        editor.apply()
    }

    override fun removeAll(cookies: Collection<Cookie>) {
        val editor = sharedPreferences.edit()
        for (cookie in cookies) {
            editor.remove(createCookieKey(cookie))
        }
        editor.apply()
    }

    private fun createCookieKey(cookie: Cookie): String {
        return (if (cookie.secure) "https" else "http") + "://" + cookie.domain + cookie.path + "|" + cookie.name
    }

    override fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}