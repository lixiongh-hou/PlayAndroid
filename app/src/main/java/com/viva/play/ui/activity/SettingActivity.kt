package com.viva.play.ui.activity

import android.content.Intent
import android.os.Bundle
import com.viva.play.App
import com.viva.play.base.BaseActivity
import com.viva.play.databinding.ActivitySettingBinding
import com.viva.play.dialog.TipDialog
import com.viva.play.service.EventBus
import com.viva.play.utils.CookieCache
import com.viva.play.utils.SettingUtils
import com.viva.play.utils.bind.binding
import com.viva.play.utils.observeEvent
import com.viva.play.utils.postValue

class SettingActivity : BaseActivity() {

    private val binding by binding<ActivitySettingBinding>()

    override fun initView(savedInstanceState: Bundle?) {
        binding.cookie = CookieCache.get().isNotBlank()
        binding.scDarkTheme.isChecked = SettingUtils.darkTheme
        binding.scDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            SettingUtils.darkTheme = isChecked
            App.initDarkMode()
        }

        binding.logout.setOnClickListener {
            TipDialog.Builder().apply {
                title = "退出登录"
                msg = "确定要退出登录吗？"
                callbackYes = {
                    CookieCache.clear()
                    postValue(EventBus.LOGIN, Any())
                    startActivity(Intent(this@SettingActivity, AuthActivity::class.java))
                }
            }.builder().show(supportFragmentManager)

        }

        observeEvent(key = EventBus.LOGIN) {
            binding.cookie = CookieCache.get().isNotBlank()
        }
    }

}