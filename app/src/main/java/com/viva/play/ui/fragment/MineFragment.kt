package com.viva.play.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.viewModels
import com.viva.play.base.BaseFragment
import com.viva.play.databinding.FragmentMineBinding
import com.viva.play.service.EventBus
import com.viva.play.service.entity.ReadLaterActivity
import com.viva.play.ui.activity.CollectionActivity
import com.viva.play.ui.activity.SettingActivity
import com.viva.play.ui.event.LoginEvent
import com.viva.play.ui.model.MineModel
import com.viva.play.utils.CookieCache
import com.viva.play.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MineFragment : BaseFragment<FragmentMineBinding>() {

    companion object {
        fun newInstance() = MineFragment()
    }

    private val model by viewModels<MineModel>()

    override fun initView(savedInstanceState: Bundle?) {
        binding.fragment = this
        binding.model = model
    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        initUserInfo()
        observeEvent(key = EventBus.LOGIN) {
            it as LoginEvent
            model.name.set(it.nickname)
            if (it.rank == -1) {
                //说明没有取到排名，从网络获取
                model.rank.set("--")
                model.coinCount.set("--")
                model.getCoinRecordInfo()
            } else {
                model.rank.set(it.rank.toString())
                model.coinCount.set(it.coinCount.toString())
            }
        }
        model.coinRecordInfo.observe(viewLifecycleOwner) {
            model.rank.set(it.rank.toString())
            model.coinCount.set(it.coinCount.toString())
        }

        observeEvent(key = EventBus.LOGIN) {
            initUserInfo()
        }
    }

    private fun initUserInfo() {
        if (CookieCache.isLogin()) {
            model.getUserInfo()
        } else {
            model.name.set("未登录")
            model.rank.set("--")
            model.coinCount.set("--")
        }
    }

    fun navigateSettingActivity() {
        startActivity(Intent(requireContext(), SettingActivity::class.java))
    }

    fun navigateAuthActivity() {
        if (CookieCache.doIfLogin(requireContext())) {
            //去个人信息界面
        }
    }

    fun navigateCollectionActivity() {
        if (CookieCache.doIfLogin(requireContext())) {
            startActivity(Intent(requireContext(), CollectionActivity::class.java))
        }
    }

    fun navigateReadLaterActivity(){
        startActivity(Intent(requireContext(), ReadLaterActivity::class.java))
    }
}