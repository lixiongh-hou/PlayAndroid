package com.viva.play.ui.fragment

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.viva.play.adapter.KnowledgeAdapter
import com.viva.play.base.BaseFragment
import com.viva.play.databinding.FragmentKnowledgeNavigationChildBinding
import com.viva.play.service.NetworkStatus
import com.viva.play.ui.model.NaviModel
import com.viva.play.utils.*
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author 李雄厚
 *
 *
 */
@AndroidEntryPoint
class NaviFragment : BaseFragment<FragmentKnowledgeNavigationChildBinding>() {

    private val model by viewModels<NaviModel>()
    private val adapter by lazy {
        KnowledgeAdapter(requireContext(), mutableListOf())
    }

    companion object {
        fun create(): NaviFragment = NaviFragment()
    }

    override fun initView(savedInstanceState: Bundle?) {
        pureScrollMode()
        binding.recyclerView.adapter = adapter
    }

    override fun initData() {
        if (NetworkUtils.isNetworkAvailable()) {
            binding.msv.postDelayed({
                if (adapter.itemCount <= 0){
                    binding.msv.toLoading()
                }
            },  500)
        }
        model.getNaviList()
        model.naviList.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.msv.toEmpty {
                    binding.msv.toLoading()
                    model.getNaviList()
                }
            } else {
                binding.msv.toContent()
                adapter.refreshData(it)
            }
        }

        adapter.itemClickListener = { data, _, _ ->
            UrlOpenUtils.with(data.link).apply {
                id = data.id
                author = data.author
                collected = data.collected
                userId = data.userId
                forceWeb = false
            }.open(requireContext())
        }

        model.error.observe(viewLifecycleOwner) {
            if (it.code == NetworkStatus.EMPTY_DATA) {
                binding.msv.toEmpty {
                    binding.msv.toLoading()
                    model.getNaviList()
                }
            } else {
                binding.msv.toError {
                    binding.msv.toLoading()
                    model.getNaviList()
                }
            }

        }
    }
}