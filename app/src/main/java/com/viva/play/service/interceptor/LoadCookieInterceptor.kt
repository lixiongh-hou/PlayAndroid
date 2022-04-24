package com.viva.play.service.interceptor

import android.text.TextUtils
import com.viva.play.utils.CookieCache
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author 李雄厚
 *
 * @features 添加登录状态拦截器
 */
class LoadCookieInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val mCookieStr: String = CookieCache.get()
        if (!TextUtils.isEmpty(mCookieStr)) {
            //长度减1为了去除最后的逗号
            builder.addHeader("Cookie", mCookieStr.substring(0, mCookieStr.length - 1))
        }
        return chain.proceed(builder.build())
    }
}