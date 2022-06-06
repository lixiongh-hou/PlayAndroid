package com.viva.play.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.flexbox.FlexboxLayoutManager
import com.viva.play.adapter.HistoryAdapter
import com.viva.play.adapter.HotAdapter
import com.viva.play.base.BaseFragment
import com.viva.play.databinding.FragmentSearchHistoryBinding
import com.viva.play.dialog.TipDialog
import com.viva.play.ui.activity.SearchActivity
import com.viva.play.ui.model.SearchHistoryModel
import com.viva.play.utils.ToastUtil.toast
import com.viva.play.utils.launchAndCollectIn
import com.viva.play.utils.solveNestQuestion
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author 李雄厚
 *
 *
 */
@AndroidEntryPoint
class SearchHistoryFragment : BaseFragment<FragmentSearchHistoryBinding>() {

    private val model by viewModels<SearchHistoryModel>()
    private val hotAdapter by lazy { HotAdapter(requireContext(), mutableListOf()) }
    private val historyAdapter by lazy { HistoryAdapter(requireContext(), mutableListOf()) }

    private var mRemoveMode = false
    private var mRemoveModeChanging = false

    companion object {
        fun create(): SearchHistoryFragment = SearchHistoryFragment()
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.rvHot.solveNestQuestion()
        binding.rvHot.layoutManager = FlexboxLayoutManager(requireContext())
        binding.rvHot.adapter = hotAdapter

        binding.rvHistory.solveNestQuestion()
        binding.rvHistory.layoutManager = FlexboxLayoutManager(requireContext())
        binding.rvHistory.adapter = historyAdapter

        binding.tvClean.setOnClickListener {
            TipDialog.Builder().apply {
                title = "清除历史"
                msg = "是否要清除历史记录？"
                callbackYes = {
                    model.delAllHistory()
                    historyAdapter.notifyItemRangeRemoved(0, historyAdapter.itemCount - 1)
                    historyAdapter.data.clear()
                    changeHistoryVisible()
                }
            }.builder().show(childFragmentManager)
        }

        binding.tvDown.setOnClickListener {
            changeRemoveMode(false)
        }


    }

    override fun initData() {
        model.getHotKeyList1()
//        model.uiState.onEach {
//            hotAdapter.refreshData(it)
//        }.launchIn(lifecycleScope)
        model.uiState.launchAndCollectIn(this) {
            hotAdapter.refreshData(it)
        }
        model.getSearchHistory()
        model.historyList.observe(viewLifecycleOwner) {
            historyAdapter.refreshData(it)
            changeHistoryVisible()
        }

        hotAdapter.clickEvent = { data, _, _ ->
            search(data.key)
        }

        historyAdapter.clickEvent = { data, _, _ ->
            if (!mRemoveMode) {
                search(data.key)
            }
        }

        historyAdapter.itemLongClick = {
            changeRemoveMode(!mRemoveMode)
        }
        historyAdapter.itemChildClick = { data, position ->
            historyAdapter.remove(position)
            model.delHistory(data)
        }
        model.error.observe(viewLifecycleOwner) {
            it.message.toast()
        }
    }

    private fun changeHistoryVisible() {
        binding.llHistory.isVisible = historyAdapter.data.isNotEmpty()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun changeRemoveMode(removeMode: Boolean) {
        if (mRemoveMode == removeMode) {
            return
        }
        mRemoveModeChanging = true
        mRemoveMode = removeMode
        historyAdapter.mRemoveMode = mRemoveMode
        historyAdapter.mRemoveModeChanging = mRemoveModeChanging
        historyAdapter.notifyDataSetChanged()
        binding.tvDown.isVisible = removeMode
        binding.tvClean.isVisible = !removeMode
    }

    private fun search(key: String) {
        if (requireActivity() is SearchActivity) {
            (requireActivity() as SearchActivity).search(key)
        }
    }

    fun addHistory(key: String) {
        model.saveSearchHistory(key)
    }
}