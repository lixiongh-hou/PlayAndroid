package com.viva.play.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.AppBarLayout
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.listener.OnMultiListener
import com.viva.play.R
import com.viva.play.adapter.ArticleAdapter
import com.viva.play.adapter.FooterAdapter
import com.viva.play.base.BaseActivity
import com.viva.play.databinding.ActivityUserPageBinding
import com.viva.play.service.EventBus
import com.viva.play.ui.event.CollectionEvent
import com.viva.play.ui.model.UserPageModel
import com.viva.play.utils.*
import com.viva.play.utils.ToastUtil.toast
import com.viva.play.utils.bind.binding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.abs

@AndroidEntryPoint
class UserPageActivity : BaseActivity() {

    private val binding by binding<ActivityUserPageBinding>()
    private val model by viewModels<UserPageModel>()
    private val adapter by lazy { ArticleAdapter(this) }

    companion object {
        private const val USER_ID = "userId"
        fun start(context: Context, userId: Int) {
            val intent = Intent(context, UserPageActivity::class.java)
            intent.putExtra(USER_ID, userId)
            context.startActivity(intent)
        }
    }

    private val userId by lazy {
        intent.getIntExtra(USER_ID, -1)
    }

    override fun initView(savedInstanceState: Bundle?) {
        pureScrollMode(binding)
        initRefresh(binding)
        binding.model = model
        model.userId = userId

        val footerAdapter = FooterAdapter(this) {
            adapter.retry()
        }
        val concatAdapter = adapter.withLoadStateFooter(footerAdapter)
        binding.recyclerView.adapter = concatAdapter
        adapter.bindLoadState(binding.msv, true)
        binding.recyclerView.bindDivider()

        binding.msv.toLoading()
        model.getUserPage(page)
        model._userPage.observe(this) {
            binding.abc.titleTextView.text = it.coinInfo.username
        }
        lifecycleScope.launchWhenCreated {
            model.pagingData.collectLatest {
                adapter.submitData(it)

            }
        }
        adapter.itemOnClick = { data, _ ->
            UrlOpenUtils.with(data.link).apply {
                title = data.title
                id = data.id
                collected = true
                author = data.author
                userId = data.userId
                forceWeb = false
            }.open(this)
        }

        adapter.collectClick = { data, _ ->
            model.id = data.id
            if (data.collect) {
                model.unCollectArticle(data.id)
            } else {
                model.collectArticle(data.id)
            }

        }
        model.collectArticle.observe(this) {
            postValue(EventBus.COLLECTED, CollectionEvent(it, model.id))
        }
        observeEvent(key = EventBus.LOGIN) {
            startRefresh()
        }

        model.error.observe(this) {
            it.message.toast()
        }

        binding.mSmartRefreshLayout.setOnMultiListener(object : OnMultiListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {}

            override fun onLoadMore(refreshLayout: RefreshLayout) {}

            override fun onStateChanged(
                refreshLayout: RefreshLayout,
                oldState: RefreshState,
                newState: RefreshState
            ) {
            }

            override fun onHeaderMoving(
                header: RefreshHeader?,
                isDragging: Boolean,
                percent: Float,
                offset: Int,
                headerHeight: Int,
                maxDragHeight: Int
            ) {
                binding.ivBlur.layoutParams.height = binding.rlUserInfo.measuredHeight + offset
                binding.ivBlur.requestLayout()
            }

            override fun onHeaderReleased(
                header: RefreshHeader?,
                headerHeight: Int,
                maxDragHeight: Int
            ) {
            }

            override fun onHeaderStartAnimator(
                header: RefreshHeader?,
                headerHeight: Int,
                maxDragHeight: Int
            ) {
            }

            override fun onHeaderFinish(header: RefreshHeader?, success: Boolean) {}
            override fun onFooterMoving(
                footer: RefreshFooter?,
                isDragging: Boolean,
                percent: Float,
                offset: Int,
                footerHeight: Int,
                maxDragHeight: Int
            ) {
                binding.ivBlur.layoutParams.height = binding.rlUserInfo.measuredHeight - offset
                binding.ivBlur.requestLayout()
            }

            override fun onFooterReleased(
                footer: RefreshFooter?,
                footerHeight: Int,
                maxDragHeight: Int
            ) {
            }

            override fun onFooterStartAnimator(
                footer: RefreshFooter?,
                footerHeight: Int,
                maxDragHeight: Int
            ) {
            }

            override fun onFooterFinish(footer: RefreshFooter?, success: Boolean) {}

        })

        binding.abl.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, offset ->
            if (abs(offset) == binding.abl.totalScrollRange) {
                binding.abc.titleTextView.alpha = 1f
                val color: Int = this.getThemeColor(R.attr.colorMainOrSurface)
                binding.abc.setBackgroundColor(color)
                binding.rlUserInfo.alpha = 1f
            } else {
                binding.abc.titleTextView.alpha = 0f
                val color: Int = this.getThemeColor(R.attr.colorTransparent)
                binding.abc.setBackgroundColor(color)
                binding.rlUserInfo.alpha =
                    1f - abs(offset).toFloat() / binding.abl.totalScrollRange.toFloat()
            }
        })
        binding.ctl.post {
            binding.ctl.minimumHeight = binding.abc.actionBar.height
            binding.ctl.scrimVisibleHeightTrigger = binding.abc.actionBar.height
        }
    }

    override fun startRefresh() {
        super.startRefresh()
        model.getUserPage(page)
        adapter.refresh()
    }

}