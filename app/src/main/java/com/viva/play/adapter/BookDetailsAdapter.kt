package com.viva.play.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.viva.play.R
import com.viva.play.base.BaseViewHolder
import com.viva.play.base.paging.BasePagingDataAdapter
import com.viva.play.databinding.ItemBookChapterBinding
import com.viva.play.db.entity.PoBookDetailsEntity
import com.viva.play.utils.getThemeColor

/**
 * @author 李雄厚
 *
 *
 */
class BookDetailsAdapter(private val context: Context) :
    BasePagingDataAdapter<PoBookDetailsEntity>() {
    override fun createBinding(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ItemBookChapterHolder(
            ItemBookChapterBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )

    override fun bind(binding: RecyclerView.ViewHolder, data: PoBookDetailsEntity, position: Int) {
        if (binding is ItemBookChapterHolder) {
            binding.binding.apply {
                this.data = data
                this.position = position
                if (data.lastTime == null) {
                    tvState.setTextColor(context.getThemeColor(R.attr.colorTextThird))
                } else {
                    if (data.percentFloat >= 0.98F) {
                        tvState.setTextColor(context.getThemeColor(R.attr.colorTextAccent))
                    } else {
                        tvState.setTextColor(context.getThemeColor(R.attr.colorTextMain))
                    }
                }
                pbPercent.max = 10000
                pbPercent.progress = (data.percentFloat * pbPercent.max).toInt()
            }
            binding.binding.executePendingBindings()
        }
    }

    class ItemBookChapterHolder(binding: ItemBookChapterBinding) :
        BaseViewHolder<ItemBookChapterBinding>(binding)

}