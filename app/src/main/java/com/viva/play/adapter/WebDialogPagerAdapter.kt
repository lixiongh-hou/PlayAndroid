package com.viva.play.adapter

import android.app.Activity
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.viewpager.widget.PagerAdapter
import com.viva.play.R
import com.viva.play.db.entity.PoHomeArticleEntity
import com.viva.play.utils.web.WebContainer
import com.viva.play.utils.web.WebHolder

/**
 * @author 李雄厚
 *
 *
 */
class WebDialogPagerAdapter(
    private val mActivity: Activity,
    private val mTopUrls: List<PoHomeArticleEntity>?,
    private val mUrls: List<PoHomeArticleEntity>?
) : PagerAdapter() {

    private val mWebs = SparseArray<WebHolder>()

    fun getArticleEntity(position: Int): PoHomeArticleEntity {
        val topUrlCount = mTopUrls?.size ?: 0
        return if (!mTopUrls.isNullOrEmpty()) {
            mTopUrls[position]
        } else {
            mUrls!![position - topUrlCount]
        }

    }

    fun pauseAllWeb() {
        for (i in 0 until mWebs.size()) {
            val web = mWebs.valueAt(i) ?: continue
            web.onPause()
        }
    }

    fun destroyAllWeb() {
        for (i in 0 until mWebs.size()) {
            val web = mWebs.valueAt(i)
            web?.onDestroy(true)
        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        //第一个position是Banner，取数据时要+1
        val data = getArticleEntity(position + 1)
        val rootView =
            LayoutInflater.from(container.context).inflate(R.layout.dialog_web_vp, container, false)
        val dialogWebWc = rootView.findViewById<WebContainer>(R.id.dialogWebWc)
        val  web = WebHolder.with(mActivity, data.link, dialogWebWc)
            .setAllowOpenOtherApp(false)
            .setAllowOpenDownload(false)
            .setAllowRedirect(false)
            .setOverrideUrlInterceptor{
                true
            }.loadUrl(data.link)
        mWebs.put(position, web)
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
        val web = mWebs[position]
        web.onDestroy(false)
        mWebs.remove(position)
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view == o
    }
}