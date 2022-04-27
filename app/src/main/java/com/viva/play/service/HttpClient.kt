package com.viva.play.service

import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import com.viva.play.service.interceptor.LoadCookieInterceptor
import com.viva.play.service.interceptor.SaveCookieInterceptor
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform.Companion.INFO
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author 李雄厚
 *
 *
 */
class HttpClient {

    companion object {
        private val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            return@lazy HttpClient()
        }

        val commonService = instance.commonService
        val poetryService = instance.poetryService
    }

    private var commonService: CommonService
    private var poetryService: CommonService

    init {
        commonService = Retrofit.Builder()
            .baseUrl(Host.HOST)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(initClient())
            .build()
            .create(CommonService::class.java)

        poetryService = Retrofit.Builder()
            .baseUrl("https://v2.jinrishici.com/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(initClient())
            .build()
            .create(CommonService::class.java)
    }

    private fun initClient(timeout: Long = 15, unit: TimeUnit = TimeUnit.SECONDS): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(timeout, unit)
            .writeTimeout(timeout, unit)
            .connectTimeout(timeout, unit)
            .addInterceptor(SaveCookieInterceptor())
            .addInterceptor(LoadCookieInterceptor())
            .addInterceptor(
                LoggingInterceptor.Builder()
                    .setLevel(Level.BASIC)
                    .log(INFO)
                    .build()
            )
            .build()
    }
}