package com.viva.play.utils.web.interceptor

import com.viva.play.utils.web.interceptor.WebHttpClient.stringRespBody
import okhttp3.Call

/**
 * @author CuiZhen
 * @date 2020/2/25
 */
abstract class BaseWebUrlInterceptor : WebUrlInterceptor {

    private val callList = arrayListOf<Call?>()

    protected fun Call.resp(): String? {
        callList.add(this)
        val resp = stringRespBody()
        callList.remove(this)
        return resp
    }

    override fun cancel() {
        callList.forEach { it?.cancel() }
        callList.clear()
    }
}