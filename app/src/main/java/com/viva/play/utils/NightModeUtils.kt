package com.viva.play.utils

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

/**
 * @author 李雄厚
 *
 *
 */

object NightModeUtils {

    fun isDarkMode(config: Configuration): Boolean {
        val uiMode = config.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return uiMode == Configuration.UI_MODE_NIGHT_YES
    }

    fun isDarkMode(context: Context): Boolean {
        return isDarkMode(context.resources.configuration)
    }
    fun initNightMode() {
        if (SettingUtils.darkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

    }
}