package com.viva.play.ui.fragment

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.viva.play.adapter.ArticleAdapter
import com.viva.play.adapter.FooterAdapter
import com.viva.play.base.BaseFragment
import com.viva.play.databinding.FragmentKnowledgeArticleBinding
import com.viva.play.service.EventBus
import com.viva.play.ui.event.CollectionEvent
import com.viva.play.ui.model.ChapterArticleModel
import com.viva.play.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

/**
 * @author 李雄厚
 *
 *
 */
@AndroidEntryPoint
class KnowledgeArticleFragment : BaseFragment<FragmentKnowledgeArticleBinding>() {

    companion object {
        private const val POSITION = "position"
        private const val CHAPTER_ID = "chapterId"
        fun create(position: Int, chapterId: Int): KnowledgeArticleFragment =
            KnowledgeArticleFragment().apply {
                arguments = Bundle().apply {
                    putInt(POSITION, position)
                    putInt(CHAPTER_ID, chapterId)
                }
            }
    }

    private val position by lazy {
        arguments?.getInt(POSITION, -1) ?: -1
    }

    private val chapterId by lazy {
        arguments?.getInt(CHAPTER_ID, -1) ?: -1
    }

    private val model by viewModels<ChapterArticleModel>()
    private val adapter by lazy { ArticleAdapter(requireContext()) }

    override fun initView(savedInstanceState: Bundle?) {
        model.cid = chapterId
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

    override fun initData() {
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