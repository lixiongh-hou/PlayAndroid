package com.viva.play.ui.fragment

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.paging.ExperimentalPagingApi
import com.viva.play.adapter.ArticleAdapter
import com.viva.play.adapter.FooterAdapter
import com.viva.play.base.BaseFragment
import com.viva.play.databinding.FragmentQuestionBinding
import com.viva.play.service.EventBus
import com.viva.play.ui.event.CollectionEvent
import com.viva.play.ui.model.QuestionMode
import com.viva.play.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionFragment : BaseFragment<FragmentQuestionBinding>() {

    companion object {
        fun newInstance() = QuestionFragment()
    }

    private val model by viewModels<QuestionMode>()
    private val adapter by lazy { ArticleAdapter(requireContext()) }

    override fun initView(savedInstanceState: Bundle?) {
        pureScrollMode()
        initRefresh()
        val footerAdapter = FooterAdapter(requireContext()) {
            adapter.retry()
        }
        val concatAdapter = adapter.withLoadStateFooter(footerAdapter)
        binding.recyclerView.adapter = concatAdapter
        adapter.bindLoadState(binding.msv)
        binding.recyclerView.bindDivider()
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun initData() {
        model.pagingData.launchAndCollectLatestIn(viewLifecycleOwner) {
            adapter.submitData(it)
        }

        adapter.itemOnClick = { data, _ ->
            UrlOpenUtils.with(data.link).apply {
                title = data.title
                id = data.id
                collected = data.collect
                author = data.author
                userId = data.userId
                forceWeb = false
            }.open(requireContext())
        }

        adapter.collectClick = { data, _ ->
            model.id = data.id
            if (data.collect) {
                model.unCollectArticle(data.id)
            } else {
                model.collectArticle(data.id)
            }

        }
        model.collectArticle.observe(viewLifecycleOwner) {
            postValue(EventBus.COLLECTED, CollectionEvent(it, model.id))
        }
        observeEvent(key = EventBus.LOGIN) {
            startRefresh()
        }
    }

    override fun startRefresh() {
        super.startRefresh()
        adapter.refresh()
    }
}