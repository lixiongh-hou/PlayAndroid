package com.viva.play.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.google.android.material.appbar.AppBarLayout
import com.viva.play.R
import com.viva.play.adapter.BookDetailsAdapter
import com.viva.play.adapter.FooterAdapter
import com.viva.play.base.BaseActivity
import com.viva.play.databinding.ActivityBookDetailsBinding
import com.viva.play.service.entity.BookEntity
import com.viva.play.ui.model.BookDetailsModel
import com.viva.play.utils.NetworkUtils
import com.viva.play.utils.bind.binding
import com.viva.play.utils.bindDivider
import com.viva.play.utils.getThemeColor
import com.viva.play.utils.toLoading
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.abs

@AndroidEntryPoint
class BookDetailsActivity : BaseActivity() {

    private val binding by binding<ActivityBookDetailsBinding>()
    private val model by viewModels<BookDetailsModel>()
    private val adapter by lazy { BookDetailsAdapter(this) }

    companion object {
        private const val PARAM_BOOK = "book"
        fun start(context: Context, bookBean: BookEntity) {
            context.startActivity(Intent(context, BookDetailsActivity::class.java).apply {
                putExtra(PARAM_BOOK, bookBean as Parcelable)
            })
        }
    }

    private val bookEntity: BookEntity? by lazy {
        intent.getParcelableExtra(PARAM_BOOK) as BookEntity?
    }

    override fun initView(savedInstanceState: Bundle?) {
        if (bookEntity == null) {
            finish()
        }
        pureScrollMode(binding)
        initRefresh(binding)
        binding.data = bookEntity
        binding.abc.titleTextView.text = bookEntity!!.name
        val footerAdapter = FooterAdapter(this) {
            adapter.retry()
        }
        val concatAdapter = adapter.withLoadStateFooter(footerAdapter)
        binding.recyclerView.adapter = concatAdapter
        adapter.bindLoadState(binding.msv)
        if (NetworkUtils.isNetworkAvailable()) {
            binding.msv.toLoading()
        }
        binding.recyclerView.bindDivider()
        binding.abl.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { abl, offset ->
            if (abs(offset) == abl.totalScrollRange) {
                binding.abc.titleTextView.alpha = 1f
                val color = binding.abc.getThemeColor(R.attr.colorMainOrSurface)
                binding.abc.setBackgroundColor(color)
                binding.llTop.alpha = 1F
            } else {
                binding.abc.titleTextView.alpha = 0f
                val color = binding.abc.getThemeColor(R.attr.colorTransparent)
                binding.abc.setBackgroundColor(color)
                binding.llTop.alpha = 1f - (abs(offset).toFloat() / abl.totalScrollRange.toFloat())
            }
        })
        binding.ctl.post {
            binding.ctl.minimumHeight = binding.abc.actionBar.height
            binding.ctl.scrimVisibleHeightTrigger = binding.abc.actionBar.height
        }
    }

    @ExperimentalPagingApi
    override fun onResume() {
        super.onResume()
        lifecycleScope.launchWhenResumed {
            model.pagingData(bookEntity!!.id).collectLatest {
                adapter.submitData(it)
            }
        }
    }

}