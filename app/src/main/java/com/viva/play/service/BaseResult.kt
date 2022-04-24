package com.viva.play.service

/**
 * @author 李雄厚
 *
 */
sealed class BaseResult<out T> {
    data class Success<out T>(val value: T) : BaseResult<T>()
    data class Failure(val throwable: ApiError?) : BaseResult<Nothing>()
}

inline fun <reified T> BaseResult<T>.doSuccess(success: (T) -> Unit) {
    if (this is BaseResult.Success) {
        success.invoke(value)
    }
}

inline fun <reified T> BaseResult<T>.doFailure(error: (ApiError?) -> Unit) {
    if (this is BaseResult.Failure) {
        error.invoke(throwable)
    }
}