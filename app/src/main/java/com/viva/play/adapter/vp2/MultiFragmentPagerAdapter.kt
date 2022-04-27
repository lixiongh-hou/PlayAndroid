package com.viva.play.adapter.vp2

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * @author 李雄厚
 *
 *
 */
class MultiFragmentPagerAdapter<E, F : Fragment>(
    fragmentActivity: FragmentActivity,
    creator: FragmentCreator<E, F>
) : FragmentStateAdapter(fragmentActivity) {

    private val mCreator: FragmentCreator<E, F> = creator
    private val mDataList = mutableListOf<E>()

    @SuppressLint("NotifyDataSetChanged")
    fun setDataList(dataList: List<E>) {
        mDataList.clear()
        mDataList.addAll(dataList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = mDataList.size
    override fun createFragment(position: Int): Fragment {
        return mCreator.create(mDataList[position], position)
    }

    interface FragmentCreator<E, F> {
        fun create(data: E, pos: Int): F
    }
}