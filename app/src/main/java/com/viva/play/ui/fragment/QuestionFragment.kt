package com.viva.play.ui.fragment

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.viva.play.adapter.ArticleAdapter
import com.viva.play.base.BaseFragment
import com.viva.play.databinding.FragmentQuestionBinding
import com.viva.play.service.EventBus
import com.viva.play.ui.event.CollectionEvent
import com.viva.play.ui.model.QuestionMode
import com.viva.play.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

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
        binding.recyclerView.cancelAnimation()
        binding.recyclerView.isFocusable = false
        binding.recyclerView.adapter = adapter
        adapter.bindLoadState(binding.msv)
    }

    @ExperimentalPagingApi
    override fun initData() {
        binding.msv.toLoading()
        lifecycleScope.launchWhenCreated {
            model.pagingData.collectLatest {
                adapter.submitData(it)
            }
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