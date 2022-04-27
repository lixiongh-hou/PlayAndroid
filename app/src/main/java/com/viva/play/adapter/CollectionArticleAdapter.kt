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
            author = data.author,
            chapterName = data.chapterName,
            collect = true,
            desc = data.desc,
            envelopePic = data.envelopePic,
            fresh = false,
            id = data.id,
            link = data.link,
            niceDate = data.niceDate,
            publishTime = data.publishTime,
            shareUser = "",
            superChapterName = "",
            tags = null,
            title = data.title,
            type = -1,
            userId = data.userId,
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