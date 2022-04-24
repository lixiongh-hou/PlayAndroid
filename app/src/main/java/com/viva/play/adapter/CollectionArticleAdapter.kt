package com.viva.play.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.viva.play.base.BasePagingDataAdapter
import com.viva.play.base.BaseViewHolder
import com.viva.play.databinding.ItemHomeArticleBinding
import com.viva.play.db.entity.PoArticleEntity
import com.viva.play.db.entity.PoCollectArticleEntity

/**
 * @author 李雄厚
 *
 *
 */
class CollectionArticleAdapter(
    private val context: Context
) : BasePagingDataAdapter<PoCollectArticleEntity>() {

    var collectClick: ((PoCollectArticleEntity, Int) -> Unit)? = null

    override fun createBinding(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemHomeArticleHolder(
            ItemHomeArticleBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun bind(
        binding: RecyclerView.ViewHolder,
        data: PoCollectArticleEntity,
        position: Int
    ) {
        val tmpData = PoArticleEntity(
            data.author,
            -1,
            data.chapterName,
            true,
            -1,
            data.desc,
            data.envelopePic,
            false,
            data.id,
            data.link,
            data.niceDate,
            "",
            "",
            data.publishTime,
            -1,
            -1,
            -1L,
            "",
            -1,
            "",
            null,
            data.title,
            -1,
            data.userId
        )
        if (binding is ItemHomeArticleHolder) {
            binding.binding.apply {
                this.data = tmpData
                cvCollect.onClick = {
                    collectClick?.invoke(data, position)
                }
            }
            binding.binding.executePendingBindings()
        }
    }

    class ItemHomeArticleHolder(binding: ItemHomeArticleBinding) :
        BaseViewHolder<ItemHomeArticleBinding>(binding)
}