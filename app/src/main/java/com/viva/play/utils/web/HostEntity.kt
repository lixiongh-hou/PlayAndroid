package com.viva.play.utils.web

/**
 * @author 李雄厚
 *
 *
 */
data class HostEntity(
    val host: String,
    val custom: Boolean = false,
    val enable: Boolean = true
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HostEntity

        if (host != other.host) return false
        if (custom != other.custom) return false
        if (enable != other.enable) return false

        return true
    }

    override fun hashCode(): Int {
        var result = host.hashCode()
        result = 31 * result + custom.hashCode()
        result = 31 * result + enable.hashCode()
        return result
    }
}