package com.viva.play.ui.activity

import android.os.Bundle
import android.view.MotionEvent
import com.viva.play.R
import com.viva.play.adapter.vp2.FixedFragmentPagerAdapter
import com.viva.play.base.BaseActivity
import com.viva.play.databinding.ActivityCollectionBinding
import com.viva.play.ui.fragment.CollectionArticleFragment
import com.viva.play.ui.fragment.CollectionLinkFragment
import com.viva.play.utils.bind.binding
import com.viva.play.utils.getThemeColor
import dagger.hilt.android.AndroidEntryPoint
import github.xuqk.kdtablayout.KDTabAdapter
import github.xuqk.kdtablayout.KDTabLayout
import github.xuqk.kdtablayout.widget.KDTab
import github.xuqk.kdtablayout.widget.KDTabIndicator
import github.xuqk.kdtablayout.widget.tab.KDColorMorphingTextTab

@AndroidEntryPoint
class CollectionActivity : BaseActivity() {

    private val binding by binding<ActivityCollectionBinding>()

    private lateinit var mAdapter: FixedFragmentPagerAdapter
    var dispatchTouchEvent: ((MotionEvent?) -> Unit)? = null

    override fun initView(savedInstanceState: Bundle?) {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.mi.tabMode = KDTabLayout.TAB_MODE_SCROLLABLE
        mAdapter = FixedFragmentPagerAdapter(
            this, listOf(
                CollectionArticleFragment.create(),
                CollectionLinkFragment.create()
            )
        )
        binding.vp.adapter = mAdapter
        binding.mi.contentAdapter = object : KDTabAdapter() {
            val tabName = listOf("文章", "网址")
            override fun createTab(position: Int): KDTab {
                return KDColorMorphingTextTab(this@CollectionActivity, tabName[position]).apply {
                    horizontalPadding = 8F
                    selectedTextSize = 17F
                    normalTextSize = 15F
                    selectedTextColor =
                        this@CollectionActivity.getThemeColor(R.attr.colorOnMainOrSurface)
                    normalTextColor =
                        this@CollectionActivity.getThemeColor(R.attr.colorOnMainOrSurfaceAlpha)
                    setOnClickListener {
                        binding.vp.currentItem = position
                    }
                }
            }

            override fun createIndicator(): KDTabIndicator? {
                return null
            }

            override fun getTabCount(): Int {
                return tabName.size
            }
        }
        // 与ViewPager2联动
        binding.mi.setViewPager2(binding.vp)

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        dispatchTouchEvent?.invoke(ev)
        return super.dispatchTouchEvent(ev)
    }

}