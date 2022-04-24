package com.viva.play.utils

import android.content.Context
import android.content.Intent
import com.viva.play.service.FileKey.COOKIE
import com.viva.play.service.FileKey.USER_ID
import com.viva.play.ui.activity.AuthActivity

/**
 * @author 李雄厚
 *
 * @features 持久化存储的登录有关
 */
object CookieCache {
    private var cookie by Preference(COOKIE, "")
    var userId by Preference(USER_ID, -1)

    fun get(): String {
        return cookie
    }

    fun set(Cookie: String) {
        this.cookie = Cookie
    }

    fun clear() {
        val prefs = Preference<Any>()
        prefs.remove(COOKIE)
        prefs.remove(USER_ID)
    }

    fun isLogin(): Boolean {
        if (cookie.isBlank()) {
            return false
        }
        return true
    }

    fun doIfLogin(context: Context): Boolean {
        return if (isLogin()) {
            true
        } else {
            context.startActivity(Intent(context, AuthActivity::class.java))
            false
        }
    }
}