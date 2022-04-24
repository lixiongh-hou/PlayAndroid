package com.viva.play.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.viewpager.widget.PagerAdapter
import com.viva.play.R
import com.viva.play.db.entity.PoArticleEntity

/**
 * @author 李雄厚
 *
 *
 */
class WebDialogPagerAdapter(
    private val mTopUrls: List<PoArticleEntity>?,
    private val mUrls: List<PoArticleEntity>?
) : PagerAdapter() {


    fun getArticleEntity(position: Int): PoArticleEntity {
        val topUrlCount = mTopUrls?.size ?: 0
        return if (!mTopUrls.isNullOrEmpty()) {
            mTopUrls[position]
        } else {
            mUrls!![position - topUrlCount]
        }

    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        //第一个position是Banner，取数据时要+1
        val data = getArticleEntity(position + 1)
        val rootView =
            LayoutInflater.from(container.context).inflate(R.layout.dialog_web_vp, container, false)
        val dialogWebWc = rootView.findViewById<AppCompatTextView>(R.id.dialogWebWc)
        dialogWebWc.text = data.author
        container.addView(rootView)
        return rootView

    }

    override fun getCount(): Int {
        var count = 0
        if (mTopUrls != null) {
            count += mTopUrls.size - 1
        }
        if (mUrls != null) {
            count += mUrls.size
        }
        return count
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view == o
    }
}