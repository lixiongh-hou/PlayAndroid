package com.viva.play.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.viva.play.R
import com.viva.play.utils.SettingUtils

/**
 * @author 李雄厚
 *
 *
 */
abstract class BaseActivity : AppCompatActivity() {
    companion object {
        val TAG: String = this::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        onCreateStart(savedInstanceState)
        super.onCreate(savedInstanceState)
        initView(savedInstanceState)
    }

    open fun onCreateStart(savedInstanceState: Bundle?) {}
    abstract fun initView(savedInstanceState: Bundle?)
}