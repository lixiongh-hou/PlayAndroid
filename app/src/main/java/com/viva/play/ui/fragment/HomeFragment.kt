package com.viva.play.ui.fragment

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.viva.play.adapter.HomeArticleAdapter
import com.viva.play.base.BaseFragment
import com.viva.play.databinding.FragmentHomeBinding
import com.viva.play.dialog.WebDialog
import com.viva.play.service.EventBus
import com.viva.play.ui.event.CollectionEvent
import com.viva.play.ui.model.HomeModel
import com.viva.play.utils.*
import com.viva.play.utils.ToastUtil.toast
import dagger.hilt.android.AndroidEntryPoint
import per.goweii.anylayer.Layer

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val mHomeModel by viewModels<HomeModel>()

    private lateinit var adapter: HomeArticleAdapter

    private var mWebDialog: WebDialog? = null

    companion object {
        fun newInstance() = HomeFragment()
    }

    override fun initView(savedInstanceState: Bundle?) {
        pureScrollMode()
        initRefresh()
        adapter = HomeArticleAdapter(requireContext()) {
            adapter.setFooterType(HomeArticleAdapter.LOADING)
            mHomeModel.getArticle(page)
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.bindDivider()
        setupScrollListener()
    }

    override fun initData() {
        mHomeModel.getHomeData(local = true)
        mHomeModel.homeData.observe(viewLifecycleOwner) {
            adapter.refreshData(it.getHomeData())
            successAfter(adapter.itemCount)
        }

        mHomeModel.article.observe(viewLifecycleOwner) {
            adapter.addData(it)
            mWebDialog?.notifyDataSetChanged()
            successAfter(adapter.itemCount)
        }

        mHomeModel.loadError.observe(viewLifecycleOwner) {
            adapter.setFooterType(HomeArticleAdapter.LOAD_FAILED)
            failureAfter()
            it.message.toast()
        }
        mHomeModel.error.observe(viewLifecycleOwner) {
            it.message.toast()
        }

        adapter.itemOnClick = { data, _ ->
            UrlOpenUtils.with(data).open(requireContext())
        }

        adapter.itemLongClick = { _, position ->
            showWebDialog(position)
            true
        }

        adapter.collectClick = { view, data ->
            if (CookieCache.doIfLogin(requireContext())) {
                if (data.collect) {
                    mHomeModel.unCollectArticle(data.id)
                } else {
                    mHomeModel.collectArticle(data.id)
                }
                data.collect = !data.collect
                view.setChecked(!view.isChecked, true)
                view.toggle()
            }
        }

        observeEvent(key = EventBus.COLLECTED) {
            it as CollectionEvent
            if (isDetached) {
                return@observeEvent
            }
            if (it.id == 0) {
                return@observeEvent
            }
            run b@{
                adapter.data.forEachIndexed { index, entity ->
                    if (entity.id == it.id) {
                        entity.collect = it.collect
                        adapter.notifyItemChanged(index)
                        return@b
                    }
                }
            }
        }
        observeEvent(key = EventBus.LOGIN) {
            startRefresh()
        }

    }

    override fun startRefresh() {
        super.startRefresh()
        mHomeModel.getHomeData(refresh = true)
    }

    override fun startLoadMore() {
        super.startLoadMore()
        mHomeModel.getArticle(page)
    }

    override fun onPause() {
        super.onPause()
        adapter.banner?.isAutoPlay(false)
        adapter.banner?.stopAutoPlay()
    }

    override fun resume() {
        super.resume()
        adapter.banner?.isAutoPlay(true)
        adapter.banner?.startAutoPlay()
    }

    private fun setupScrollListener() {
        val layoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                if (adapter.itemCount > totalItemCount) {
                    adapter.setShowFooter(false)
                    return
                }
                mHomeModel.listScrolled(visibleItemCount, lastVisibleItem, totalItemCount) {
                    startLoadMore()
                }
            }
        })
    }

    private fun showWebDialog(position: Int) {
        //第一个position是Banner，取数据时要-1
        mWebDialog =
            WebDialog.create(
                context = requireContext(),
                currPos = position - 1,
                urls = adapter.data,
                singleTipMode = false
            )
        mWebDialog?.onPageChanged = { _, data ->
            var currItemPos = 0
            run b@{
                adapter.data.forEachIndexed { index, poArticleEntity ->
                    if (poArticleEntity.id == data.id) {
                        currItemPos = index
                        return@b
                    }
                }
            }
            if (currItemPos < 0) {
                currItemPos = 0
            }
            binding.recyclerView.smoothScrollToPosition(currItemPos)
        }

        mWebDialog?.collectClick = { view, data ->
            if (CookieCache.doIfLogin(requireContext())) {
                if (data.collect) {
                    mHomeModel.unCollectArticle(data.id)
                } else {
                    mHomeModel.collectArticle(data.id)
                }
                data.collect = !data.collect
                view.setChecked(!view.isChecked, true)
                view.toggle()
                run b@{
                    adapter.data.forEachIndexed { index, entity ->
                        if (entity.id == data.id) {
                            entity.collect = data.collect
                            adapter.notifyItemChanged(index)
                            return@b
                        }
                    }
                }

            }

        }

        mWebDialog?.onDismissListener(object : Layer.OnDismissListener {
            override fun onDismissing(layer: Layer) {}

            override fun onDismissed(layer: Layer) {
                mWebDialog = null
            }
        })
        mWebDialog?.show()
    }

}