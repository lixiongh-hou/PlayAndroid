package com.viva.play.ui.fragment

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.viva.play.adapter.CollectionArticleAdapter
import com.viva.play.adapter.FooterAdapter
import com.viva.play.base.BaseFragment
import com.viva.play.databinding.FragmentCollectionArticleBinding
import com.viva.play.service.EventBus
import com.viva.play.ui.event.CollectionEvent
import com.viva.play.ui.model.CollectionModel
import com.viva.play.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

/**
 * @author 李雄厚
 *
 *
 */
@AndroidEntryPoint
class CollectionArticleFragment : BaseFragment<FragmentCollectionArticleBinding>() {

    private val model by viewModels<CollectionModel>()
    private val adapter by lazy { CollectionArticleAdapter(requireContext()) }
    private var visible = true

    companion object {
        fun create() = CollectionArticleFragment()
    }

    override fun initView(savedInstanceState: Bundle?) {
        pureScrollMode()
        initRefresh()
        val footerAdapter = FooterAdapter(requireContext()) {
            adapter.retry()
        }
        val concatAdapter = adapter.withLoadStateFooter(footerAdapter)
        binding.recyclerView.adapter = concatAdapter
        binding.recyclerView.bindDivider()
        adapter.bindLoadState(binding.msv)
    }

    override fun startRefresh() {
        super.startRefresh()
        adapter.refresh()
    }

    @ExperimentalPagingApi
    override fun initData() {
        lifecycleScope.launchWhenCreated {
            model.pagingData.collectLatest {
                adapter.submitData(it)
            }
        }

        adapter.itemOnClick = { data, _ ->
            visible = false
            UrlOpenUtils.with(data.link).apply {
                title = data.title
                id = data.originId
                collected = true
                author = data.author
                userId = data.userId
                forceWeb = false
            }.open(requireContext())
        }
        adapter.collectClick = { data, _ ->
            visible = true
            model.id = data.id
            model.originId = data.originId
            model.unMyCollectArticle()
        }
        model.unMyCollectArticle.observe(viewLifecycleOwner) {
            postValue(EventBus.COLLECTED, CollectionEvent(it, model.originId))
        }

    }

}