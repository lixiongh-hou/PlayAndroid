package com.viva.play.service

import com.viva.play.base.BaseResponse
import retrofit2.HttpException
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

/**
 * @author 李雄厚
 *
 *
 */
object DataConvert {
    /**
     * 解析网络错误信息
     */
    fun Throwable.convertNetworkError(failure: (ApiError)->Unit) {
        failure(
            when (this) {
                is SocketTimeoutException -> ApiError(
                    -1,
                    "网络请求超时"
                )
                is ConnectException -> ApiError(
                    -1,
                    "网络连接超时"
                )
                is SSLHandshakeException -> ApiError(
                    -1,
                    "安全证书异常"
                )
                is UnknownHostException -> ApiError(
                    -1,
                    "域名解析失败"
                )
                is HttpException -> {
                    when {
                        this.code() >= 500 -> ApiError(
                            this.code(),
                            "服务器错误"
                        )
                        this.code() in 400..499 -> ApiError(
                            this.code(),
                            "客户端请求地址错误"
                        )
                        else -> {
                            ApiError(
                                this.code(),
                                "未知错误"
                            )
                        }
                    }
                }
                is NoRouteToHostException -> ApiError(
                    -1,
                    "网络不通"
                )
                else -> {
                    ApiError(-1, "未知错误")
                }
            }
        )
    }

    /**
     * 解析接口返回的数据和错误信息
     */
    fun <T> BaseResponse<T>.convert(success: (T?) -> Unit) {
        if (this.success()) {
            success(this.data)
        } else {
            when(this.errorCode){
                NetworkStatus.LOGIN_ERROR -> throw ServerException(
                    ApiError(
                        this.errorCode,
                        this.errorMsg ?: "登录失效，重新登录"
                    )
                )
                else -> {
                    throw ServerException(
                        ApiError(
                            this.errorCode,
                            this.errorMsg ?: ""
                        )
                    )
                }
            }
        }
    }
}