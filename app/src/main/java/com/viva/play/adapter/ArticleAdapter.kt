package com.viva.play.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.viva.play.base.paging.BasePagingDataAdapter
import com.viva.play.databinding.ItemHomeArticleBinding
import com.viva.play.db.entity.PoArticleEntity
import com.viva.play.ui.activity.KnowledgeArticleActivity
import com.viva.play.ui.activity.UserPageActivity
import com.viva.play.utils.CookieCache

/**
 * @author 李雄厚
 *
 *
 */
class ArticleAdapter(
    private val context: Context
) : BasePagingDataAdapter<PoArticleEntity>() {

    var collectClick: ((PoArticleEntity, Int) -> Unit)? = null

    override fun createBinding(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CollectionArticleAdapter.ItemHomeArticleHolder(
            ItemHomeArticleBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun bind(binding: RecyclerView.ViewHolder, data: PoArticleEntity, position: Int) {
        if (binding is CollectionArticleAdapter.ItemHomeArticleHolder) {
            binding.binding.apply {
                this.data = data
                this.cvCollect.onClick = {
                    if (CookieCache.doIfLogin(context)) {
                        collectClick?.invoke(data, position)
                    }
                }

                tvAuthor.setOnClickListener {
                    UserPageActivity.start(context, data.userId)
                }

                tvTag.setOnClickListener {
                    KnowledgeArticleActivity.start(context, data.tags?.get(0))
                }
            }
            binding.binding.executePendingBindings()
        }
    }


}