package com.viva.play.adapter

import android.animation.ObjectAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.viva.play.base.BaseViewHolder
import com.viva.play.base.paging.BasePagingDataAdapter
import com.viva.play.databinding.ItemReadLaterBinding
import com.viva.play.db.entity.PoReadLaterEntity

/**
 * @author 李雄厚
 *
 *
 */
class ReadLaterAdapter(
    private val context: Context
) : BasePagingDataAdapter<PoReadLaterEntity>() {

    var onDelete: ((PoReadLaterEntity, Int) -> Unit)? = null
    var onCopy: ((PoReadLaterEntity, Int) -> Unit)? = null
    var onOpen: ((PoReadLaterEntity, Int) -> Unit)? = null

    private var lastPosition = -1
    var recyclerView: RecyclerView? = null

    override fun createBinding(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ReadLaterHolder(ItemReadLaterBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun bind(binding: RecyclerView.ViewHolder, data: PoReadLaterEntity, position: Int) {
        if (binding is ReadLaterHolder) {
            binding.binding.data = data
            binding.binding.executePendingBindings()
            binding.itemView.setOnLongClickListener {
                if (lastPosition != position || binding.binding.swipeLayout.isGone) {
                    binding.binding.swipeLayout.isVisible = true
                    val animation =
                        ObjectAnimator.ofFloat(binding.binding.swipeLayout, "alpha", 0F, 1F)
                    animation.duration = 300
                    animation.start()
                }
                lastPosition = position
                true
            }

            binding.binding.tvCopy.setOnClickListener {
                onCopy?.invoke(data, position)
            }
            binding.binding.tvOpen.setOnClickListener {
                onOpen?.invoke(data, position)
            }
            binding.binding.tvDelete.setOnClickListener {
                onDelete?.invoke(data, position)
            }
        }
    }

    fun clearAnimation() {
        if (recyclerView != null && lastPosition != -1) {
            val view = recyclerView!!.getChildAt(lastPosition)
            val tmpViewHolder = recyclerView!!.getChildViewHolder(view)
            if (tmpViewHolder != null) {
                if (tmpViewHolder is ReadLaterHolder) {
                    if (tmpViewHolder.binding.swipeLayout.isVisible) {
                        val animation =
                            ObjectAnimator.ofFloat(
                                tmpViewHolder.binding.swipeLayout,
                                "alpha",
                                1F,
                                0F
                            )
                        animation.duration = 100
                        animation.start()
                        animation.doOnEnd {
                            lastPosition = -1
                            tmpViewHolder.binding.swipeLayout.isVisible = false
                        }
                    }

                }
            }
        }
    }

    inner class ReadLaterHolder(binding: ItemReadLaterBinding) :
        BaseViewHolder<ItemReadLaterBinding>(binding)
}