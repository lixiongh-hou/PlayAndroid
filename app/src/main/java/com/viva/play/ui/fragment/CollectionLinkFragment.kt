package com.viva.play.ui.fragment

import android.os.Bundle
import android.view.MotionEvent
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.viva.play.adapter.CollectionLinkAdapter
import com.viva.play.adapter.FooterAdapter
import com.viva.play.base.BaseFragment
import com.viva.play.databinding.FragmentCollectionLinkBinding
import com.viva.play.db.entity.PoCollectLinkEntity
import com.viva.play.dialog.EditCollectLinkDialog
import com.viva.play.ui.activity.CollectionActivity
import com.viva.play.ui.model.CollectionLinkModel
import com.viva.play.utils.*
import com.viva.play.utils.ToastUtil.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

/**
 * @author 李雄厚
 *
 *
 */
@AndroidEntryPoint
class CollectionLinkFragment : BaseFragment<FragmentCollectionLinkBinding>() {

    private val adapter by lazy { CollectionLinkAdapter(requireContext()) }
    private val model by viewModels<CollectionLinkModel>()

    companion object {
        fun create() = CollectionLinkFragment()
    }

    override fun initView(savedInstanceState: Bundle?) {
        pureScrollMode()
        initRefresh()
        val footerAdapter = FooterAdapter(requireContext()) {
            adapter.retry()
        }
        val concatAdapter = adapter.withLoadStateFooter(footerAdapter)
        binding.recyclerView.adapter = concatAdapter
        adapter.recyclerView = binding.recyclerView
        binding.recyclerView.bindDivider()
        adapter.bindLoadState(binding.msv, true)

        (requireActivity() as CollectionActivity).dispatchTouchEvent = {
            it?.let { event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    adapter.clearAnimation()
                }
            }
        }

    }


    override fun startRefresh() {
        super.startRefresh()
        adapter.refresh()
    }

    override fun initData() {
        binding.msv.toLoading()
        lifecycleScope.launchWhenCreated {
            model.pagingData.collectLatest {
                adapter.submitData(it)
            }
        }

        adapter.itemOnClick = { data, _ ->
            UrlOpenUtils.with(data.url).apply {
                title = data.title
                collected = true
                collectId = data.collectId
                forceWeb = true
            }.open(requireContext())
        }
        adapter.onCopy = { data, _ ->
            CopyUtils.copyText(data.url)
            "复制成功".toast()
        }
        adapter.onOpen = b@{ data, _ ->
            if (data.url.isBlank()) {
                "链接为空".toast()
                return@b
            }
            IntentUtils.openBrowser(requireContext(), data.url)
        }
        adapter.onEdit = { data, _ ->
            model.id = data.collectId
            EditCollectLinkDialog.Builder().apply {
                name = data.title
                link = data.url
                setOnConfirmClick { name, link ->
                    model.editCollectLink(PoCollectLinkEntity(model.id, name, link))
                }.builder().show(childFragmentManager)
            }
        }
        adapter.onDelete = { data, _ ->
            model.id = data.collectId
            model.unCollectLink()
        }
        model.error.observe(viewLifecycleOwner) {
            it.message.toast()
        }
    }


}