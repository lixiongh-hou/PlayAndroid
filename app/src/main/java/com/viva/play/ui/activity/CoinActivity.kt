package com.viva.play.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.viva.play.adapter.CoinRecordAdapter
import com.viva.play.adapter.FooterAdapter
import com.viva.play.base.BaseActivity
import com.viva.play.databinding.ActivityCoinBinding
import com.viva.play.ui.model.CoinModel
import com.viva.play.utils.AnimatorUtils
import com.viva.play.utils.bind.binding
import com.viva.play.utils.bindDivider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class CoinActivity : BaseActivity() {

    private val binding by binding<ActivityCoinBinding>()
    private val mode by viewModels<CoinModel>()
    private val adapter by lazy { CoinRecordAdapter(this) }


    override fun initView(savedInstanceState: Bundle?) {
        pureScrollMode(binding)
        val footerAdapter = FooterAdapter(this) {
            adapter.retry()
        }
        val concatAdapter = adapter.withLoadStateFooter(footerAdapter)
        binding.recyclerView.adapter = concatAdapter
        adapter.bindLoadState(binding.msv, isMediator = false)
        binding.recyclerView.bindDivider()
    }

    override fun onResume() {
        super.onResume()
        mode.getCoin()
        lifecycleScope.launchWhenResumed {
            mode.pagingData.collectLatest {
                adapter.submitData(it)
            }
        }
        mode.coin.observe(this) {
            AnimatorUtils.doIntAnim(binding.tvCoin, it, 1000)
        }
    }

}