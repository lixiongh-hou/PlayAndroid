package com.viva.play

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
import com.viva.play.utils.pressBackTwiceToExitApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    val binding by binding<ActivityMainBinding>()

    private val mMainModel by viewModels<MainModel>()

    private lateinit var mMinePage: TabFragmentPagerAdapter.Page<TabEntity>
    private lateinit var mTabFragmentPagerAdapter: TabFragmentPagerAdapter<TabEntity>


    override fun initView(savedInstanceState: Bundle?) {
        installSplashScreen()
        pressBackTwiceToExitApp("要退出应用程序，请再次按下")
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

//        listOf(1, 2, 3).asFlow().onEach {
//            if (it == 3){
//                throw Exception("test")
//            }
//            println("$it")
//        }.catch {
//            println("test")
//        }.launchIn(lifecycleScope)
//        println("Done")

//        main()
        //创建
//        val signEvent = MutableSharedFlow<String>()
//        //监听
//        lifecycleScope.launch{
//            signEvent.collect{ value->
//                println(value)
//            }
//        }
//
//        //赋值
//        signEvent.tryEmit("hello")
//        signEvent.tryEmit("shared flow")
    }

//    private fun main() = runBlocking {
//        withTimeoutOrNull(250) {
//            simple().collect { value -> println(value) }
//        }
//        println("Done")
//    }
//
//    private fun simple(): Flow<Int> = flow {
//        (1..3).forEach {
//            delay(200)
//            println("Emitting $it")
//            emit(it)
//        }
//    }

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