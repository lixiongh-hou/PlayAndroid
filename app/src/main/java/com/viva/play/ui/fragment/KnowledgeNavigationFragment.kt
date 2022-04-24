package com.viva.play.ui.fragment

import android.os.Bundle
import com.viva.play.R
import com.viva.play.adapter.vp2.FixedFragmentPagerAdapter
import com.viva.play.base.BaseFragment
import com.viva.play.databinding.FragmentKnowledgeNavigationBinding
import com.viva.play.utils.getThemeColor
import dagger.hilt.android.AndroidEntryPoint
import github.xuqk.kdtablayout.KDTabAdapter
import github.xuqk.kdtablayout.KDTabLayout
import github.xuqk.kdtablayout.widget.KDTab
import github.xuqk.kdtablayout.widget.KDTabIndicator
import github.xuqk.kdtablayout.widget.tab.KDColorMorphingTextTab

@AndroidEntryPoint
class KnowledgeNavigationFragment : BaseFragment<FragmentKnowledgeNavigationBinding>() {

    private lateinit var mAdapter: FixedFragmentPagerAdapter

    companion object {
        fun newInstance() = KnowledgeNavigationFragment()
    }

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initData() {
        binding.mi.tabMode = KDTabLayout.TAB_MODE_SCROLLABLE
        mAdapter = FixedFragmentPagerAdapter(
            requireActivity(), listOf(
                KnowledgeFragment.create(),
                NaviFragment.create()
            )
        )
        binding.vp.adapter = mAdapter
        binding.mi.contentAdapter = object : KDTabAdapter() {
            val tabName = listOf("体系", "导航")
            override fun createTab(position: Int): KDTab {
                return KDColorMorphingTextTab(requireContext(), tabName[position]).apply {
                    horizontalPadding = 8F
                    selectedTextSize = 17F
                    normalTextSize = 15F
                    selectedTextColor =
                        requireContext().getThemeColor(R.attr.colorOnMainOrSurface)
                    normalTextColor =
                        requireContext().getThemeColor(R.attr.colorOnMainOrSurfaceAlpha)
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
}