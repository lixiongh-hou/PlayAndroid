package com.viva.play.dialog

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import com.viva.play.R
import com.viva.play.adapter.WebDialogPagerAdapter
import com.viva.play.db.entity.PoHomeArticleEntity
import com.viva.play.utils.ToastUtil.toast
import com.viva.play.utils.dpToPx
import com.viva.play.views.CollectView
import per.goweii.anylayer.dialog.DialogLayer
import per.goweii.anylayer.utils.AnimatorHelper

/**
 * @author 李雄厚
 *
 *
 */
class WebDialog(
    context: Context,
    private val currPos: Int,
    private val singleTipMode: Boolean,
    topUrls: List<PoHomeArticleEntity>?,
    urls: List<PoHomeArticleEntity>?
) : DialogLayer(context) {

    private var mAdapter: WebDialogPagerAdapter
    var onPageChanged: ((Int, PoHomeArticleEntity) -> Unit)? = null
    var collectClick: ((CollectView, PoHomeArticleEntity) -> Unit)? = null

    companion object {
        fun create(
            context: Context,
            currPos: Int = 0,
            singleTipMode: Boolean = false,
            topUrls: List<PoHomeArticleEntity>? = null,
            urls: List<PoHomeArticleEntity>? = null
        ): WebDialog = WebDialog(context, currPos, singleTipMode, topUrls, urls)
    }

    init {
        contentView(R.layout.dialog_web)
        backgroundDimDefault()
        cancelableOnTouchOutside(true)
        cancelableOnClickKeyBack(true)
        onClickToDismiss(R.id.dialogWebIvClose)
        contentAnimator(object : AnimatorCreator {
            override fun createInAnimator(target: View): Animator {
                val vp = getView<ViewPager>(R.id.dialogWebVp)
                val bar = target.findViewById<RelativeLayout>(R.id.dialogWebRlBottomBar)
                bar.translationY = 1000f
                vp!!.pageMargin = 12.dpToPx
                val vpMargin = ValueAnimator.ofInt(vp.pageMargin, 0)
                vpMargin.interpolator = DecelerateInterpolator()
                vpMargin.addUpdateListener {
                    val value = it.animatedValue as Int
                    vp.pageMargin = value
                }
                vpMargin.startDelay = 150
                vpMargin.duration = 300
                val barAnim = AnimatorHelper.createBottomInAnim(bar)
                barAnim.duration = 300
                barAnim.startDelay = 150
                val vpAlpha = AnimatorHelper.createBottomInAnim(vp)
                vpAlpha.startDelay = 0
                vpAlpha.duration = 300
                val set = AnimatorSet()
                set.playTogether(vpMargin, vpAlpha, barAnim)
                return set
            }

            override fun createOutAnimator(target: View): Animator {
                val vp = getView<ViewPager>(R.id.dialogWebVp)
                val bar = target.findViewById<RelativeLayout>(R.id.dialogWebRlBottomBar)
                val vpMargin = ValueAnimator.ofInt(vp!!.pageMargin, vp.pageMargin)
                vpMargin.interpolator = AccelerateInterpolator()
                vpMargin.addUpdateListener {
                    val value = it.animatedValue as Int
                    vp.pageMargin = value
                }
                vpMargin.startDelay = 0
                vpMargin.duration = 300
                val barAnim = AnimatorHelper.createBottomOutAnim(bar)
                barAnim.duration = 300
                barAnim.startDelay = 0
                val vpAlpha = AnimatorHelper.createBottomOutAnim(vp)
                vpAlpha.startDelay = 150
                vpAlpha.duration = 300
                val set = AnimatorSet()
                set.playTogether(vpMargin, vpAlpha, barAnim)
                return set
            }
        })
        mAdapter = WebDialogPagerAdapter(topUrls, urls)
    }

    fun notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged()
    }

    override fun onAttach() {
        super.onAttach()
        val vp = requireView<ViewPager>(R.id.dialogWebVp)
        val webIvReadLater = requireView<AppCompatImageView>(R.id.dialogWebIvReadLater)
        val webCvCollect = requireView<CollectView>(R.id.dialogWebCvCollect)
        if (singleTipMode) {
            webIvReadLater.isVisible = false
            webCvCollect.isVisible = false
        } else {
            webIvReadLater.isVisible = true
            webCvCollect.isVisible = true
            webIvReadLater.setOnClickListener {
                "稍后阅读".toast()
            }
            webCvCollect.onClick = {
                //第一个position是Banner，取数据时要+1
                val data = mAdapter.getArticleEntity(vp.currentItem + 1)
                collectClick?.invoke(it, data)
                //                data.collect = !data.collect
//                it.isChecked = !data.collect
//                it.toggle()
//                "收藏".toast()
            }
            vp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    //第一个position是Banner，取数据时要+1
                    val data = mAdapter.getArticleEntity(position + 1)
                    webCvCollect.setChecked(data.collect, true)
                    onPageChanged?.invoke(position, data)
                }

                override fun onPageScrollStateChanged(state: Int) {}

            })
            //第一个position是Banner，取数据时要+1
            val data = mAdapter.getArticleEntity(currPos + 1)
            webCvCollect.setChecked(data.collect, true)
            vp.adapter = mAdapter
            vp.currentItem = currPos
        }
    }
}