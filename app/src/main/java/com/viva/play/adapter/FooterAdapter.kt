package com.viva.play.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.viva.play.base.BaseViewHolder
import com.viva.play.databinding.ListFooterBinding

/**
 * @author 李雄厚
 *
 *
 */
class FooterAdapter(
    private val context: Context,
    private val retry: () -> Unit) : LoadStateAdapter<BaseViewHolder<ListFooterBinding>>() {

    override fun onBindViewHolder(holder: BaseViewHolder<ListFooterBinding>, loadState: LoadState) {
        when (loadState) {
            is LoadState.Loading -> {
                holder.binding.footerProgress.isVisible = true
                holder.binding.footerMessage.text = "正在加载..."
            }
            is LoadState.Error -> {
                holder.binding.footerProgress.isVisible = false
                holder.binding.footerMessage.text = "加载失败点击重试~"
                holder.binding.root.setOnClickListener {
                    retry.invoke()
                }
            }
            else -> {

            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): BaseViewHolder<ListFooterBinding> =
        BaseViewHolder(ListFooterBinding.inflate(LayoutInflater.from(context), parent, false))

}