package com.viva.play.utils

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.viva.play.App

/**
 * Created by LXH on 2021/11/2.
 */
object PermissionUtil {

    fun checkPermissionGranted(vararg permission: String): Boolean {
        permission.forEach {
            if (!isGranted(it)) {
                return false
            }
        }
        return true
    }

    private fun isGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            App.instance,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}