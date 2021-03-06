package com.viva.play.ui.activity

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.viva.play.adapter.FooterAdapter
import com.viva.play.adapter.ReadLaterAdapter
import com.viva.play.base.BaseActivity
import com.viva.play.databinding.ActivityReadLaterBinding
import com.viva.play.dialog.TipDialog
import com.viva.play.ui.model.ReadLaterModel
import com.viva.play.utils.*
import com.viva.play.utils.bind.binding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ReadLaterActivity : BaseActivity() {

    private val binding by binding<ActivityReadLaterBinding>()
    private val model by viewModels<ReadLaterModel>()
    private val adapter by lazy { ReadLaterAdapter(this) }

    override fun initView(savedInstanceState: Bundle?) {
        pureScrollMode(binding)
        initRefresh(binding)
        val footerAdapter = FooterAdapter(this) {
            adapter.retry()
        }
        val concatAdapter = adapter.withLoadStateFooter(footerAdapter)
        binding.recyclerView.adapter = concatAdapter
        adapter.recyclerView = binding.recyclerView
        binding.recyclerView.bindDivider()
        adapter.bindLoadState(binding.msv, isLoading = false)

        binding.abc.setOnRightTextClickListener {
            TipDialog.Builder().apply {
                title = "清除书签"
                msg = "是否要清除全部书签？"
                callbackYes = {
                    model.removeReadLaterAll()
                }
            }.builder().show(supportFragmentManager)
        }

        lifecycleScope.launchWhenCreated {
            model.pagingData.collectLatest {
                adapter.submitData(it)
            }
        }

        adapter.itemOnClick = { data, _ ->
            UrlOpenUtils.with(data.link).apply {
                title = data.title
            }.open(this)
        }

        adapter.onCopy = { data, _ ->
            CopyUtils.copyText(data.link)
            toast("复制成功")
        }
        adapter.onOpen = b@{ data, _ ->
            if (data.link.isBlank()) {
                toast("链接为空")
                return@b
            }
            openBrowser(data.link)
        }
        adapter.onDelete = { data, _ ->
            model.removeReadLater(data.link)
        }
        model.error.observe(this) {
            toast(it.message)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            adapter.clearAnimation()
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun startRefresh() {
        super.startRefresh()
        adapter.refresh()
    }

}