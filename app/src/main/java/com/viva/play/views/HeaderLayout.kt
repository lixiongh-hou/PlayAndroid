package com.viva.play.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.appcompat.widget.AppCompatTextView
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.simple.SimpleComponent
import com.viva.play.R
import com.viva.play.di.MadeInPoetry
import com.viva.play.service.CommonService
import com.viva.play.utils.RefreshHolder
import com.viva.play.utils.dpToPx
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * @author 李雄厚
 *
 *
 */
@AndroidEntryPoint
class HeaderLayout(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    SimpleComponent(
        context,
        attrs,
        defStyleAttr
    ), RefreshHeader {

    companion object {
        const val REFRESH_HEADER_PULLING = "下拉刷新"
        const val REFRESH_HEADER_REFRESHING = "正在刷新"
        const val REFRESH_HEADER_RELEASE = "释放刷新"
        const val REFRESH_HEADER_FINISH = "刷新完成"
        const val REFRESH_HEADER_FAILED = "刷新失败"
    }

    private var textView: AppCompatTextView
    private var taoBaoView: TaoBaoView
    private var refreshingAnimation: RotateAnimation
    private var scope: CoroutineScope? = null

    @MadeInPoetry
    @Inject
    lateinit var poetryService: CommonService

    init {
        val padding = 10.dpToPx
        setPadding(0, padding, 0, padding)
        View.inflate(context, R.layout.refresh_header, this)
        textView = findViewById(R.id.reTextView)
        taoBaoView = findViewById(R.id.taoBaoView)
        refreshingAnimation =
            AnimationUtils.loadAnimation(context, R.anim.rotating) as RotateAnimation
        val lir = LinearInterpolator()
        refreshingAnimation.interpolator = lir
        taoBaoView.setProgress(90)
        scope = GlobalScope
        scope?.launch {
            if (RefreshHolder.get().isNullOrEmpty()) {
                RefreshHolder.request(poetryService)
            }
        }
    }

    private fun textPulling(): String {
        val string = RefreshHolder.get()
        return if (string.isNullOrEmpty()) REFRESH_HEADER_PULLING else string
    }

    private fun textRefreshing(): String {
        val string = RefreshHolder.get()
        return if (string.isNullOrEmpty()) REFRESH_HEADER_REFRESHING else string
    }

    private fun textRelease(): String {
        val string = RefreshHolder.get()
        return if (string.isNullOrEmpty()) REFRESH_HEADER_RELEASE else string
    }

    private fun textFinish(): String {
        val string = RefreshHolder.get()
        return if (string.isNullOrEmpty()) REFRESH_HEADER_FINISH else string
    }

    private fun textFailed(): String {
        val string = RefreshHolder.get()
        return if (string.isNullOrEmpty()) REFRESH_HEADER_FAILED else string
    }

    fun setTextAndHideIcon(text: String) {
        refreshingAnimation.cancel()
        taoBaoView.alpha = 0F
        textView.text = text
    }

    fun restoreToCurrState() {
        taoBaoView.alpha = 1F
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        refreshingAnimation.cancel()
        scope = null
    }

    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        if (success) {
            val str = textFinish()
            textView.text = str
        } else {
            val str = textFailed()
            textView.text = str
        }
        scope?.launch {
            RefreshHolder.refresh(poetryService)
        }
        taoBaoView.clearAnimation()
        return 0
    }

    override fun onStateChanged(
        refreshLayout: RefreshLayout,
        oldState: RefreshState,
        newState: RefreshState
    ) {
        super.onStateChanged(refreshLayout, oldState, newState)
        when (newState) {
            RefreshState.None -> {
                textView.text = ""
            }
            RefreshState.PullDownToRefresh -> {
                val str = textPulling()
                textView.text = str
                taoBaoView.setIsShowIcon(true)
            }
            RefreshState.ReleaseToRefresh -> {
                val str = textRelease()
                textView.text = str
                taoBaoView.setIsShowIcon(false)
            }
            RefreshState.RefreshReleased -> {
                val str = textRefreshing()
                textView.text = str
                taoBaoView.setIsShowIcon(false)
                taoBaoView.startAnimation(refreshingAnimation)
            }
            else -> {
            }
        }

    }

    override fun onMoving(
        isDragging: Boolean,
        percent: Float,
        offset: Int,
        height: Int,
        maxDragHeight: Int
    ) {
        super.onMoving(isDragging, percent, offset, height, maxDragHeight)
        if (isDragging) {
            val progress = (percent.coerceAtMost(1.0f) * 100)
            taoBaoView.setProgress(if (progress > 90) 90 else progress.toInt())
        }
    }

}