package com.viva.play.ui.fragment

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.viva.play.adapter.KnowledgeAdapter
import com.viva.play.base.BaseFragment
import com.viva.play.databinding.FragmentKnowledgeNavigationChildBinding
import com.viva.play.service.NetworkStatus
import com.viva.play.ui.model.KnowledgeModel
import com.viva.play.utils.toContent
import com.viva.play.utils.toEmpty
import com.viva.play.utils.toError
import com.viva.play.utils.toLoading
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author 李雄厚
 *
 *
 */
@AndroidEntryPoint
class KnowledgeFragment : BaseFragment<FragmentKnowledgeNavigationChildBinding>() {

    private val model by viewModels<KnowledgeModel>()
    private val adapter by lazy {
        KnowledgeAdapter(requireContext(), mutableListOf())
    }

    companion object {
        fun create(): KnowledgeFragment = KnowledgeFragment()
    }

    override fun initView(savedInstanceState: Bundle?) {
        pureScrollMode()
        binding.recyclerView.adapter = adapter
    }


    override fun initData() {
        binding.msv.toLoading()
        model.getKnowledgeList()
        model.knowledgeList.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.msv.toEmpty {
                    model.getKnowledgeList()
                }
            } else {
                binding.msv.toContent()
                adapter.refreshData(it)
            }
        }
        model.error.observe(viewLifecycleOwner) {
            if (it.code == NetworkStatus.EMPTY_DATA) {
                binding.msv.toEmpty {
                    model.getKnowledgeList()
                }
            } else {
                binding.msv.toError {
                    model.getKnowledgeList()
                }
            }

        }
    }
}