package com.viva.play.ui.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.lihang.ShadowLayout
import com.viva.play.R
import com.viva.play.base.BaseActivity
import com.viva.play.databinding.ActivityArticleBinding
import com.viva.play.db.entity.PoReadLaterEntity
import com.viva.play.service.EventBus
import com.viva.play.ui.event.CollectionEvent
import com.viva.play.ui.model.HomeModel
import com.viva.play.ui.model.WebModel
import com.viva.play.utils.*
import com.viva.play.utils.ToastUtil.toast
import com.viva.play.utils.bind.binding
import com.viva.play.utils.web.WebHolder
import com.viva.play.utils.web.cache.ReadingModeManager
import com.viva.play.utils.web.interceptor.WebReadingModeInterceptor
import com.viva.play.utils.web.interceptor.WebResUrlInterceptor
import com.viva.play.views.CollectView
import dagger.hilt.android.AndroidEntryPoint
import me.zhanghai.android.materialprogressbar.MaterialProgressBar
import per.goweii.reveallayout.RevealLayout
import per.goweii.statusbarcompat.StatusBarCompat
import per.goweii.swipeback.SwipeBackAbility

@AndroidEntryPoint
class ArticleActivity : BaseActivity(), SwipeBackAbility.OnlyEdge {

    private data class FloatIcon(
        val container: View,
        val shadow: View,
        val icon: View,
        val tip: View,
        var tipAnim: Animator? = null
    )

    private val floatIcons: List<FloatIcon> by lazy {
        mutableListOf<FloatIcon>().apply {
            add(
                FloatIcon(
                    rlIconCollect,
                    slCollect,
                    cvCollect,
                    tvCollectTip
                )
            )
            add(
                FloatIcon(
                    rlIconReadLater,
                    slReadLater,
                    aivReadLater,
                    tvReadLaterTip
                )
            )
            add(
                FloatIcon(
                    rlIconOpen,
                    slOpen,
                    aivOpen,
                    tvOpenTip
                )
            )
            add(
                FloatIcon(
                    rlIconShare,
                    slShare,
                    aivShare,
                    tvShareTip
                )
            )

        }
    }

    companion object {
        private const val URL = "url"
        private const val TITLE = "title"
        private const val ID = "id"
        private const val COLLECTED = "collected"
        private const val AUTHOR = "author"
        private const val USER_ID = "userId"
        private const val KEY = "key"

        fun start(
            context: Context,
            url: String,
            title: String,
            id: Int,
            collected: Boolean,
            author: String,
            userId: Int,
            key: String?,
        ) {
            context.startActivity(Intent(context, ArticleActivity::class.java).apply {
                putExtra(URL, url)
                putExtra(TITLE, title)
                putExtra(ID, id)
                putExtra(COLLECTED, collected)
                putExtra(AUTHOR, author)
                putExtra(USER_ID, userId)
                putExtra(KEY, key)
            })
        }
    }

    private val binding by binding<ActivityArticleBinding>()
    private val model by viewModels<WebModel>()

    //这个值的目的就是用来初始化一个值判断我是通过点击来移除还是添加稍后阅读的
    private var readLaterData: PoReadLaterEntity? = null

    private val url by lazy {
        intent.getStringExtra(URL) ?: ""
    }
    private val title by lazy {
        intent.getStringExtra(TITLE) ?: ""
    }
    private val id by lazy {
        intent.getIntExtra(ID, -1)
    }
    private val collected by lazy {
        intent.getBooleanExtra(COLLECTED, false)
    }
    private val author by lazy {
        intent.getStringExtra(AUTHOR) ?: ""
    }
    private val userId by lazy {
        intent.getIntExtra(USER_ID, -1)
    }
    private val key: String? by lazy {
        intent.getStringExtra(KEY)
    }

    private lateinit var mWebHolder: WebHolder
    private var lastUrlLoadTime = 0L
    private var userTouched = false
    private var isPageLoadFinished = false

    private var floatIconsVisible = false
    private var floatIconsAnim: AnimatorSet? = null

    private lateinit var rlIconCollect: RelativeLayout
    private lateinit var slCollect: ShadowLayout
    private lateinit var cvCollect: CollectView
    private lateinit var tvCollectTip: AppCompatTextView

    private lateinit var rlIconReadLater: RelativeLayout
    private lateinit var slReadLater: ShadowLayout
    private lateinit var aivReadLater: AppCompatImageView
    private lateinit var tvReadLaterTip: AppCompatTextView

    private lateinit var rlIconOpen: RelativeLayout
    private lateinit var slOpen: ShadowLayout
    private lateinit var aivOpen: AppCompatImageView
    private lateinit var tvOpenTip: AppCompatTextView

    private lateinit var rlIconShare: RelativeLayout
    private lateinit var slShare: ShadowLayout
    private lateinit var aivShare: AppCompatImageView
    private lateinit var tvShareTip: AppCompatTextView

    private lateinit var vBack: View
    private lateinit var flBack: FrameLayout
    private lateinit var ivClose: AppCompatImageView

    private lateinit var pb: MaterialProgressBar
    private lateinit var rl: RevealLayout

    override fun initView(savedInstanceState: Bundle?) {
        model.id.set(id)
        model.url.set(url)
        model.title.set(title)
        model.collected.set(collected)
        model.author.set(author)
        model.userId.set(userId)

        pb = binding.root.findViewById(R.id.pb)
        rl = binding.root.findViewById(R.id.rl)

        rlIconCollect = binding.root.findViewById(R.id.rlIconCollect)
        slCollect = binding.root.findViewById(R.id.slCollect)
        cvCollect = binding.root.findViewById(R.id.cvCollect)
        tvCollectTip = binding.root.findViewById(R.id.tvCollectTip)

        rlIconReadLater = binding.root.findViewById(R.id.rlIconReadLater)
        slReadLater = binding.root.findViewById(R.id.slReadLater)
        aivReadLater = binding.root.findViewById(R.id.aivReadLater)
        tvReadLaterTip = binding.root.findViewById(R.id.tvReadLaterTip)

        rlIconOpen = binding.root.findViewById(R.id.rlIconOpen)
        slOpen = binding.root.findViewById(R.id.slOpen)
        aivOpen = binding.root.findViewById(R.id.aivOpen)
        tvOpenTip = binding.root.findViewById(R.id.tvOpenTip)

        rlIconShare = binding.root.findViewById(R.id.rlIconShare)
        slShare = binding.root.findViewById(R.id.slShare)
        aivShare = binding.root.findViewById(R.id.aivShare)
        tvShareTip = binding.root.findViewById(R.id.tvShareTip)

        vBack = binding.root.findViewById(R.id.vBack)
        flBack = binding.root.findViewById(R.id.flBack)
        ivClose = binding.root.findViewById(R.id.ivClose)

        StatusBarCompat.setIconMode(this, !NightModeUtils.isDarkMode(this))
        mWebHolder = WebHolder.with(this, model.url.get()!!, binding.wc, pb)
            .setLoadCacheElseNetwork(true)
            .setUseInstanceCache(true)
            .setAllowOpenOtherApp(false)
            .setAllowOpenDownload(false)
            .setAllowRedirect(false)
            .setOverrideUrlInterceptor {
                if (!isPageLoadFinished) return@setOverrideUrlInterceptor false
                if (!userTouched) return@setOverrideUrlInterceptor false
                val currUrlLoadTime = System.currentTimeMillis()
                val intercept = if (currUrlLoadTime - lastUrlLoadTime > 1000L) {
                    UrlOpenUtils.with(it).open(this)
                    true
                } else {
                    false
                }
                lastUrlLoadTime = currUrlLoadTime
                return@setOverrideUrlInterceptor intercept
            }
            .setOnPageLoadCallback(object : WebHolder.OnPageLoadCallback {
                override fun onPageStarted() {
                }

                override fun onPageFinished() {
                    isPageLoadFinished = true
                    try {
                        val uri = Uri.parse(mWebHolder.getUrl())
                        val message = uri.getQueryParameter("scrollToKeywords")
                        if (!message.isNullOrEmpty()) {
                            mWebHolder.scrollToKeywords(message.split(","))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
            .setInterceptUrlInterceptor { uri, reqHeaders, reqMethod ->
                val pageUri = Uri.parse(model.url.get()!!)
                ReadingModeManager.getUrlRegexBeanForHost(pageUri.host)
                    ?: return@setInterceptUrlInterceptor null
                WebReadingModeInterceptor.intercept(
                    pageUri,
                    uri,
                    mWebHolder.getUserAgent(),
                    reqHeaders,
                    reqMethod
                )?.let {
                    return@setInterceptUrlInterceptor it
                }
                WebResUrlInterceptor.intercept(
                    pageUri,
                    uri,
                    mWebHolder.getUserAgent(),
                    reqHeaders,
                    reqMethod
                )?.let {
                    return@setInterceptUrlInterceptor it
                }
                return@setInterceptUrlInterceptor null
            }
            .setOnPageTitleCallback {
                if (!key.isNullOrEmpty()) {
                    model.addReadRecord(key!!, mWebHolder.getUrl(), mWebHolder.getPercent())
                }
            }
            .setOnPageScrollEndListener {
                //这里目的时移除稍后阅读
                if (readLater) {
                    model.removeReadLater(mWebHolder.getUrl())
                }
            }
            .setOnPageScrollChangeListener {
                if (!key.isNullOrEmpty()) {
                    model.updateReadRecordPercent(key!!, mWebHolder.getUrl(), it)
                }
            }

        lastUrlLoadTime = System.currentTimeMillis()
        mWebHolder.loadUrl(model.url.get())

        binding.wc.setOnDoubleClickListener { _, _ ->
            rl.setCenter(rl.width * 0.5F, rl.height * 0.5F)
            cvCollect.setCenter(rl.width * 0.5F, rl.height * 0.5F)
            if (!model.collected.get()) {
                model.collectArticle(model.id.get())
                model.collected.set(true)
                cvCollect.setChecked(model.collected.get(), true)
            }
        }
        binding.wc.setOnTouchDownListener {
            if (floatIconsVisible) {
                toggleFloatIcons()
            }
        }

        val icons = mutableListOf<FloatIconTouchListener.Icon>()
        floatIcons.forEach {
            icons.add(FloatIconTouchListener.Icon(it.icon))
        }
        vBack.setOnTouchListener(
            FloatIconTouchListener(
                icons,
                object : FloatIconTouchListener.OnFloatTouchedListener {
                    override fun onTouched(v: View?) {
                        var floatIcon: FloatIcon? = null
                        floatIcons.forEach {
                            if (it.icon == v) {
                                floatIcon = it
                            }
                        }
                        doFloatTipAnim(floatIcon)
                    }
                })
        )

        switchCollectView(false)
        cvCollect.onClick = {
            if (model.collected.get()) {
                model.unCollectArticle(model.id.get())
            } else {
                model.collectArticle(model.id.get())
            }
            model.collected.set(!model.collected.get())
            switchCollectView()
        }

        aivReadLater.setOnClickListener {
            readLaterData = PoReadLaterEntity("", "")
            model.isReadLater(model.url.get()!!)
        }

        aivOpen.setOnClickListener {
            UrlOpenUtils.with(model.url.get()!!).apply {
                title = model.title.get()!!
                id = model.id.get()
                collected = model.collected.get()
                author = model.author.get()!!
                userId = model.userId.get()
                forceWeb = true
            }.open(this)

        }

        vBack.setOnClickListener {
            if (floatIconsVisible) {
                toggleFloatIcons()
            } else {
                finish()
            }
        }
        vBack.setOnLongClickListener {
            toggleFloatIcons()
            true
        }

    }

    private fun switchCollectView(anim: Boolean = true) {
        rl.setChecked(model.collected.get(), anim)
        cvCollect.setChecked(model.collected.get(), anim)
    }


    private fun toggleFloatIcons() {
        floatIconsVisible = !floatIconsVisible

        if (!floatIconsVisible) {
            doFloatTipAnim(null)
        }
        floatIconsAnim?.cancel()
        floatIconsAnim = AnimatorSet().apply {
            val anim = mutableListOf<Animator>()
            anim.add(ObjectAnimator.ofFloat(
                flBack, "rotation",
                flBack.rotation, if (floatIconsVisible) 360F else 0F
            ).apply {
                duration = 300L
                addUpdateListener {
                    if (it.animatedFraction > 0.5F) {
                        if (floatIconsVisible) ivClose.isVisible = true
                        else ivClose.isInvisible = true
                    }
                }
            })
            floatIcons.filterIndexed { index, floatIconModel ->
                anim.add(AnimatorSet().apply {
                    duration = 300L
                    playTogether(
                        ObjectAnimator.ofFloat(
                            floatIconModel.container, "translationY",
                            floatIconModel.container.translationY,
                            if (floatIconsVisible) -floatIconModel.container.height.toFloat() * (index + 1) else 0F
                        ),
                        ObjectAnimator.ofFloat(
                            floatIconModel.shadow, "alpha",
                            floatIconModel.shadow.alpha, if (floatIconsVisible) 1F else 0F
                        )
                    )
                })
            }
            playTogether(anim)
            interpolator = DecelerateInterpolator()
        }
        floatIconsAnim?.apply {
            doOnStart {
                floatIcons.forEach { it.container.isVisible = true }
            }
            doOnEnd {
                floatIcons.forEach {
                    if (it.container.translationY == 0F) it.container.isInvisible = true
                }
            }
        }?.start()
    }

    private fun doFloatTipAnim(floatIconOnTouched: FloatIcon?) {
        floatIcons.forEach { floatIcon ->
            floatIcon.tipAnim?.cancel()
            if (floatIconOnTouched == floatIcon) {
                floatIcon.tipAnim = AnimatorSet().apply {
                    duration = 200L
                    interpolator = DecelerateInterpolator()
                    val fromX = if (floatIcon.tip.translationX == 0F) {
                        -floatIcon.icon.width.toFloat()
                    } else {
                        floatIcon.tip.translationX
                    }
                    val fromA = if (floatIcon.tip.alpha == 1F) {
                        0F
                    } else {
                        floatIcon.tip.alpha
                    }
                    playTogether(
                        ObjectAnimator.ofFloat(
                            floatIcon.tip, "translationX",
                            fromX, 0F
                        ),
                        ObjectAnimator.ofFloat(
                            floatIcon.tip, "alpha",
                            fromA, 1F
                        )
                    )
                    doOnStart {
                        floatIcon.tip.isVisible = true
                    }
                    start()
                }
            } else {
                floatIcon.tipAnim = AnimatorSet().apply {
                    duration = 200L
                    interpolator = DecelerateInterpolator()
                    val fromX = floatIcon.tip.translationX
                    val fromA = floatIcon.tip.alpha
                    playTogether(
                        ObjectAnimator.ofFloat(
                            floatIcon.tip, "translationX",
                            fromX, -floatIcon.icon.width.toFloat()
                        ),
                        ObjectAnimator.ofFloat(
                            floatIcon.tip, "alpha",
                            fromA, 0F
                        )
                    )
                    addListener(object : Animator.AnimatorListener {
                        private var endByCancel = false

                        override fun onAnimationStart(animation: Animator?) {
                            floatIcon.tip.isVisible = true
                        }

                        override fun onAnimationRepeat(animation: Animator?) {
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            if (endByCancel) return
                            if (floatIcon.tip.translationX != 0F) {
                                floatIcon.tip.isInvisible = true
                            }
                        }

                        override fun onAnimationCancel(animation: Animator?) {
                            endByCancel = true
                        }
                    })
                    start()
                }
            }
            floatIcon.tipAnim?.start()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        userTouched = true
        return super.dispatchTouchEvent(ev)
    }

    override fun onPause() {
        mWebHolder.onPause()
        super.onPause()
    }


    override fun onResume() {
        mWebHolder.onResume()
        super.onResume()
        model.isReadLater(model.url.get()!!)
        model.collectArticle.observe(this) {
            //通知首页刷新
            postValue(EventBus.COLLECTED, CollectionEvent(it, model.id.get()))
        }

        observeEvent(key = EventBus.COLLECTED) {
            model.collected.set((it as CollectionEvent).collect)
            switchCollectView()
        }

        model.error.observe(this) {
            model.collected.set(false)
            switchCollectView()
            it.message.toast()
        }


        model.isReadLater.observe(this) {
            if (readLaterData == null) {
                switchReadLaterIcon(it)
            } else {
                if (it) {
                    switchReadLaterIcon(false)
                    model.removeReadLater(mWebHolder.getUrl())
                } else {
                    switchReadLaterIcon(true)
                    model.addReadLater(
                        PoReadLaterEntity(
                            mWebHolder.getUrl(), model.title.get()!!
                        )
                    )
                }
            }
            readLaterData = null

        }
        model.addReadLater.observe(this) {
            if (it) {
                "已加入我的书签".toast()
            } else {
                "已移除我的书签".toast()
            }
        }
    }

    private var readLater = false
    private fun switchReadLaterIcon(readLater: Boolean) {
        this.readLater = readLater
        if (readLater) {
            aivReadLater.setImageResource(R.drawable.ic_read_later_added)
            aivReadLater.setColorFilter(aivReadLater.getThemeColor(R.attr.colorIconMain))
        } else {
            aivReadLater.setImageResource(R.drawable.ic_read_later)
            aivReadLater.setColorFilter(aivReadLater.getThemeColor(R.attr.colorIconSurface))
        }
    }

    override fun onDestroy() {
        WebResUrlInterceptor.cancel()
        WebReadingModeInterceptor.cancel()
        floatIconsAnim?.cancel()
        floatIcons.forEach {
            it.tipAnim?.cancel()
        }
        mWebHolder.onDestroy(false)
        super.onDestroy()
    }

    override fun swipeBackOnlyEdge(): Boolean {
        return true
    }

}