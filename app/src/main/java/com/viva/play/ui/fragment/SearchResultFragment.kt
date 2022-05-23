package com.viva.play.ui.fragment

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.viva.play.adapter.ArticleAdapter
import com.viva.play.adapter.FooterAdapter
import com.viva.play.base.BaseFragment
import com.viva.play.databinding.FragmentSearchResultBinding
import com.viva.play.ui.model.SearchResultModel
import com.viva.play.utils.bindDivider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

/**
 * @author 李雄厚
 *
 *
 */
@AndroidEntryPoint
class SearchResultFragment : BaseFragment<FragmentSearchResultBinding>() {

    private val model by viewModels<SearchResultModel>()
    private val adapter by lazy { ArticleAdapter(requireContext()) }

    companion object {
        private const val KEY = "key"
        fun create(key: String): SearchResultFragment = SearchResultFragment().apply {
            arguments = Bundle().apply {
                putString(KEY, key)
            }
        }
    }

    private val key by lazy {
        arguments?.getString(KEY) ?: ""
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
        adapter.bindLoadState(binding.msv, isMediator = false)
    }

    override fun initData() {
        lifecycleScope.launchWhenCreated {
            model.pagingData(key).collectLatest {
                adapter.submitData(it)
            }
        }
    }
}