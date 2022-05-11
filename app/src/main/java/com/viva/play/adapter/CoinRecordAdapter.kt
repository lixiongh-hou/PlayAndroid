package com.viva.play.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.viva.play.base.BaseViewHolder
import com.viva.play.base.paging.BasePagingDataAdapter
import com.viva.play.databinding.ItemCoinRecordBinding
import com.viva.play.service.entity.CoinRecord

/**
 * @author 李雄厚
 *
 *
 */
class CoinRecordAdapter(private val context: Context) : BasePagingDataAdapter<CoinRecord>() {
    override fun createBinding(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        CoinRecordViewHolder(
            ItemCoinRecordBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )

    @SuppressLint("SetTextI18n")
    override fun bind(binding: RecyclerView.ViewHolder, data: CoinRecord, position: Int) {
        val desc = data.desc
        val firstSpace = desc.indexOf(" ")
        val secondSpace = desc.indexOf(" ", firstSpace + 1)
        val time = desc.substring(0, secondSpace)
        val title = desc.substring(secondSpace + 1)
            .replace(",", "")
            .replace("：", "")
            .replace(" ", "")
        if (binding is CoinRecordViewHolder) {
            binding.binding.apply {
                tvCoinCount.text = "+${data.coinCount}"
                tvTitle.text = title
                tvTime.text = time
            }
            binding.binding.executePendingBindings()
        }
    }

    class CoinRecordViewHolder(binding: ItemCoinRecordBinding) :
        BaseViewHolder<ItemCoinRecordBinding>(binding)
}