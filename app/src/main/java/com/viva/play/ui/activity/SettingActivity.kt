package com.viva.play.ui.activity

import android.content.Intent
import android.os.Bundle
import com.viva.play.App
import com.viva.play.base.BaseActivity
import com.viva.play.databinding.ActivitySettingBinding
import com.viva.play.dialog.ListDialog
import com.viva.play.dialog.TipDialog
import com.viva.play.service.EventBus
import com.viva.play.utils.CookieCache
import com.viva.play.utils.SettingUtils
import com.viva.play.utils.bind.binding
import com.viva.play.utils.observeEvent
import com.viva.play.utils.postValue
import com.viva.play.utils.web.HostInterceptUtils

class SettingActivity : BaseActivity() {

    private val binding by binding<ActivitySettingBinding>()
    private var mUrlIntercept = 0

    override fun initView(savedInstanceState: Bundle?) {
        binding.fragment = this
        binding.cookie = CookieCache.get().isNotBlank()
        binding.scDarkTheme.isChecked = SettingUtils.darkTheme
        binding.scDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            SettingUtils.darkTheme = isChecked
            App.initDarkMode()
        }
        mUrlIntercept = SettingUtils.urlInterceptType
        binding.tvInterceptHost.text = HostInterceptUtils.getName(mUrlIntercept)

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

    fun setInterceptHost() {
        ListDialog.Builder()
            .setData(
                HostInterceptUtils.getName(HostInterceptUtils.TYPE_NOTHING),
                HostInterceptUtils.getName(HostInterceptUtils.TYPE_ONLY_WHITE),
                HostInterceptUtils.getName(HostInterceptUtils.TYPE_INTERCEPT_BLACK)
            ).apply {
                currSelectPos = SettingUtils.urlInterceptType
                itemClick = { _, position ->
                    binding.tvInterceptHost.text = HostInterceptUtils.getName(position)
                    SettingUtils.urlInterceptType = position
                }
            }.builder().show(supportFragmentManager)
    }

}