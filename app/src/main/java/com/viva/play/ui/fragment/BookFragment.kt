package com.viva.play.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import com.viva.play.adapter.BookAdapter
import com.viva.play.base.BaseFragment
import com.viva.play.databinding.FragmentBookBinding
import com.viva.play.ui.activity.BookDetailsActivity
import com.viva.play.ui.model.BookModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author 李雄厚
 *
 *
 */
@AndroidEntryPoint
class BookFragment : BaseFragment<FragmentBookBinding>() {

    private val model by viewModels<BookModel>()
    private val adapter by lazy { BookAdapter(requireContext(), mutableListOf()) }

    override fun initView(savedInstanceState: Bundle?) {
        pureScrollMode()
        binding.recyclerView.adapter = adapter

    }
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            model.getBooks()
        }
    }

    override fun initData() {
        model.books.observe(viewLifecycleOwner) {
            adapter.refreshData(it)
        }
        adapter.clickEvent = { data, _, _ ->
            BookDetailsActivity.start(requireContext(), data)
        }
    }
}