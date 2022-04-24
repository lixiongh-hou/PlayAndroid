package com.viva.play.base

import com.viva.play.service.NetworkStatus


/**
 * @author 李雄厚
 *
 * @features API接口反应体
 */
class BaseResponse<out T>(val errorCode: Int, val errorMsg: String?, val data: T?) {
    fun success(): Boolean {
        return errorCode == NetworkStatus.SUCCESS
    }
}