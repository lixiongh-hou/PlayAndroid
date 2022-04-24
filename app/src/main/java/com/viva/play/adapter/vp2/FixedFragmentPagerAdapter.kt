package com.viva.play.adapter.vp2

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * @author 李雄厚
 *
 *
 */
class FixedFragmentPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val tabFragment: List<Fragment>
) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = tabFragment.size

    override fun createFragment(position: Int): Fragment = tabFragment[position]

}