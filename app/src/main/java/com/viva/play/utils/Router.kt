package com.viva.play.utils

/**
 * @author 李雄厚
 *
 *
 */
private const val SCHEME: String = "wana"
private const val HOST = "www.wanandroid.com"
fun String.createUrlByPath(): String {
    val urlBuilder = StringBuilder()
    urlBuilder.append(SCHEME)
    urlBuilder.append("://")
    urlBuilder.append(HOST)
    if (isNotEmpty()){
        if (!startsWith("/")){
            urlBuilder.append("/")
        }
        urlBuilder.append(this)
    }
    return urlBuilder.toString()
}