package com.viva.play.adapter.vp2

import android.annotation.SuppressLint
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

/**
 * @author 李雄厚
 *
 *
 */
class TabFragmentPagerAdapter<T>(
    fragmentActivity: FragmentActivity,
    private val mViewPager: ViewPager2,
    private val mTabContainer: LinearLayoutCompat,
    private val mTabItemRes: Int,
    private var mPages: Array<Page<T>>? = null
) : FragmentStateAdapter(fragmentActivity) {

    var lastPosition: Int = -1

    init {
        mViewPager.adapter = this
        mViewPager.isUserInputEnabled = false
        mViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (lastPosition != -1) {
                    val page = mPages!![position]
                    page.notifyAdapterBindData(view = page.view, data = page.data, true)

                    if (lastPosition >= 0 && lastPosition <= mPages!!.size - 1) {
                        val page1 = mPages!![lastPosition]
                        page1.notifyAdapterBindData(view = page1.view, data = page1.data, false)
                    }
                }
                lastPosition = position
            }
        })
    }

    fun setPages(pages: Array<Page<T>>, refresh: Boolean = false) {
        mViewPager.offscreenPageLimit = pages.size
        mTabContainer.removeAllViews()
        mPages = pages
        mPages!!.forEach { page ->
            initPageTab(page)
        }
        notifyPageDataChanged(refresh)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initPageTab(page: Page<T>) {
        page.view =
            LayoutInflater.from(mTabContainer.context).inflate(mTabItemRes, mTabContainer, false)
        val params = page.view.layoutParams as LinearLayoutCompat.LayoutParams
        params.height = LinearLayout.LayoutParams.MATCH_PARENT
        params.width = 0
        params.weight = 1F
        mTabContainer.addView(page.view, params)
        val mGestureDetector =
            GestureDetector(page.view.context, object : GestureDetector.OnGestureListener {
                override fun onDown(e: MotionEvent): Boolean {
                    return true
                }

                override fun onShowPress(e: MotionEvent) {}
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    switchCurrentItem(page)
                    return true
                }

                override fun onScroll(
                    e1: MotionEvent,
                    e2: MotionEvent,
                    distanceX: Float,
                    distanceY: Float
                ): Boolean {
                    return false
                }

                override fun onLongPress(e: MotionEvent) {}
                override fun onFling(
                    e1: MotionEvent,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    return false
                }
            })
        mGestureDetector.setOnDoubleTapListener(object : GestureDetector.OnDoubleTapListener {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                return false
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                page.notifyAdapterDoubleTap()
                return true
            }

            override fun onDoubleTapEvent(e: MotionEvent): Boolean {
                return false
            }
        })
        page.view.setOnTouchListener { _, event -> mGestureDetector.onTouchEvent(event) }
    }


    private fun switchCurrentItem(page: Page<T>) {
        for (i in 0 until itemCount) {
            val fragment = mPages!![i].fragment
            if (page.fragment == fragment) {
                mViewPager.setCurrentItem(i, false)
                break
            }
        }
    }

    private fun notifyPageDataChanged(refresh: Boolean) {
        var currPos = mViewPager.currentItem
        if (refresh) {
            currPos = 3
        }
        mPages?.forEachIndexed { index, page ->
            page.notifyAdapterBindData(selected = currPos == index)
        }
    }


    override fun getItemCount(): Int = mPages?.size ?: 0

    override fun createFragment(position: Int): Fragment = mPages!![position].fragment

    class Page<T>(
        val fragment: Fragment,
        val data: T,
        private val adapter: TabAdapter<T>
    ) {
        lateinit var view: View

        fun notifyAdapterBindData(view: View? = null, data: T? = null, selected: Boolean) {
            adapter.onBindData(view ?: this.view, data ?: this.data, selected)
        }

        fun notifyAdapterDoubleTap() {
            adapter.onDoubleTap(fragment)
        }

        interface TabAdapter<T> {
            fun onBindData(view: View, data: T, selected: Boolean)

            fun onDoubleTap(fragment: Fragment)
        }
    }
}