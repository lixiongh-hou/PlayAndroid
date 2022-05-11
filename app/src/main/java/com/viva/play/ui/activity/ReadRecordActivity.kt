package com.viva.play.ui.activity

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.viva.play.adapter.FooterAdapter
import com.viva.play.adapter.ReadRecordAdapter
import com.viva.play.base.BaseActivity
import com.viva.play.databinding.ActivityReadRecordBinding
import com.viva.play.dialog.TipDialog
import com.viva.play.ui.model.ReadRecordModel
import com.viva.play.utils.CopyUtils
import com.viva.play.utils.IntentUtils
import com.viva.play.utils.ToastUtil.toast
import com.viva.play.utils.UrlOpenUtils
import com.viva.play.utils.bind.binding
import com.viva.play.utils.bindDivider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import per.goweii.actionbarex.common.OnActionBarChildClickListener

@AndroidEntryPoint
class ReadRecordActivity : BaseActivity() {

    private val binding by binding<ActivityReadRecordBinding>()
    private val model by viewModels<ReadRecordModel>()
    private val adapter by lazy { ReadRecordAdapter(this) }

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

    override fun onResume() {
        super.onResume()
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
            "复制成功".toast()
        }
        adapter.onOpen = b@{ data, _ ->
            if (data.link.isBlank()) {
                "链接为空".toast()
                return@b
            }
            IntentUtils.openBrowser(this, data.link)
        }

        adapter.onDelete = { data, _ ->
            model.delReadRecord(data)
        }

        binding.abc.setOnRightTextClickListener {
            TipDialog.Builder().apply {
                msg = "确定要全部删除吗?"
                callbackYes = {
                    model.delAllReadRecord()
                }
            }.builder().show(supportFragmentManager)
        }

        model.error.observe(this) {
            it.message.toast()
        }

    }

}