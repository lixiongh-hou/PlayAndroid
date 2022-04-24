package com.viva.play

import android.os.Bundle
import androidx.activity.viewModels
import com.viva.play.adapter.vp2.MainTabAdapter
import com.viva.play.adapter.vp2.TabEntity
import com.viva.play.adapter.vp2.TabFragmentPagerAdapter
import com.viva.play.base.BaseActivity
import com.viva.play.databinding.ActivityMainBinding
import com.viva.play.ui.fragment.HomeFragment
import com.viva.play.ui.fragment.KnowledgeNavigationFragment
import com.viva.play.ui.fragment.MineFragment
import com.viva.play.ui.fragment.QuestionFragment
import com.viva.play.ui.model.MainModel
import com.viva.play.utils.SettingUtils
import com.viva.play.utils.bind.binding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private val binding by binding<ActivityMainBinding>()

    private val mMainModel by viewModels<MainModel>()

    private lateinit var mMinePage: TabFragmentPagerAdapter.Page<TabEntity>
    private lateinit var mTabFragmentPagerAdapter: TabFragmentPagerAdapter<TabEntity>


    override fun initView(savedInstanceState: Bundle?) {
        mTabFragmentPagerAdapter = TabFragmentPagerAdapter(
            this,
            binding.vpTab,
            binding.llBottomBar,
            R.layout.tab_item_main
        )
        setTabLayout(
            SettingUtils.darkTheme,
            if (mMainModel.isDarkTheme == null) false else mMainModel.isDarkTheme != SettingUtils.darkTheme
        )
        mMainModel.isDarkTheme = SettingUtils.darkTheme
    }


    private fun setTabLayout(isNight: Boolean = false, refresh: Boolean) {
        mMinePage = TabFragmentPagerAdapter.Page(
            MineFragment.newInstance(),
            TabEntity("我的", "lottie_me.json", "lottie_me_night.json", isNight, -1),
            MainTabAdapter()
        )
        mTabFragmentPagerAdapter.setPages(
            arrayOf(
                TabFragmentPagerAdapter.Page(
                    HomeFragment.newInstance(),
                    TabEntity(
                        "首页",
                        "lottie_position.json",
                        "lottie_position_night.json",
                        isNight,
                        -1
                    ),
                    MainTabAdapter()
                ),
                TabFragmentPagerAdapter.Page(
                    QuestionFragment.newInstance(),
                    TabEntity(
                        "问答", "lottie_discover.json",
                        "lottie_discover_night.json",
                        isNight,
                        -1
                    ),
                    MainTabAdapter()
                ),
                TabFragmentPagerAdapter.Page(
                    KnowledgeNavigationFragment.newInstance(),
                    TabEntity(
                        "体系",
                        "lottie_message.json",
                        "lottie_message_night.json",
                        isNight,
                        -1
                    ),
                    MainTabAdapter()
                ),
                mMinePage
            ), refresh
        )
    }
}