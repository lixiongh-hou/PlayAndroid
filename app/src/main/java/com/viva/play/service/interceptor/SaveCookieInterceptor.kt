package com.viva.play.service.interceptor

import com.viva.play.service.Url.Login
import com.viva.play.utils.CookieCache
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author 李雄厚
 *
 * @features 保存登录状态拦截器
 */
class SaveCookieInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        val mCookieList = response.headers("Set-Cookie")
        if (mCookieList.isNotEmpty() && request.url.toString().endsWith(Login)) {
            val sb = StringBuilder()
            for (cookie in mCookieList) {
                //注意Cookie请求头字段中的每个Cookie之间用逗号或分号分隔
                sb.append(cookie).append(",")
            }
            CookieCache.set(sb.toString())
        }
        return response
    }
}