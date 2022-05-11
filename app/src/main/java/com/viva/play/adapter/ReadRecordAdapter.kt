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
import com.viva.play.databinding.ItemReadRecordBinding
import com.viva.play.db.entity.PoReadRecordEntity

/**
 * @author 李雄厚
 *
 *
 */
class ReadRecordAdapter(private val context: Context) :
    BasePagingDataAdapter<PoReadRecordEntity>() {

    var onDelete: ((PoReadRecordEntity, Int) -> Unit)? = null
    var onCopy: ((PoReadRecordEntity, Int) -> Unit)? = null
    var onOpen: ((PoReadRecordEntity, Int) -> Unit)? = null

    private var lastPosition = -1
    var recyclerView: RecyclerView? = null

    override fun createBinding(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ReadRecordHolder(ItemReadRecordBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun bind(binding: RecyclerView.ViewHolder, data: PoReadRecordEntity, position: Int) {
        if (binding is ReadRecordHolder) {
            binding.binding.apply {
                this.data = data
            }
            binding.binding.executePendingBindings()
            binding.itemView.setOnLongClickListener {
                if (lastPosition != position || binding.binding.swipeLayout.isGone) {
                    binding.binding.swipeLayout.isVisible = true
                    val animation =
                        ObjectAnimator.ofFloat(binding.binding.swipeLayout, "alpha", 0F, 1F)
                    animation.duration = 300
                    animation.start()
                }
                //[position]会因为删除数据后不会进行改变
                //当下标有0、1、2时候，删除了下标1，此时下标应该是0、1，但是再选择1的时候还是下标2
                val a1 = snapshot().items
                lastPosition = a1.indexOf(data)
//                lastPosition = position
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
            lastPosition = -1
            val tmpViewHolder = recyclerView!!.getChildViewHolder(view)
            if (tmpViewHolder != null) {
                if (tmpViewHolder is CollectionLinkAdapter.CollectionLinkHolder) {
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

    class ReadRecordHolder(binding: ItemReadRecordBinding) :
        BaseViewHolder<ItemReadRecordBinding>(binding)
}