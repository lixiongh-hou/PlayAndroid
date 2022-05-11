package com.viva.play.base.paging

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * @author 李雄厚
 *
 *
 */
abstract class BasePagingDataAdapter<T : Any> :
    PagingDataAdapter<T, RecyclerView.ViewHolder>(Comparator()) {

    var itemOnClick: ((T, Int) -> Unit)? = null


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        data?.let {
            bind(holder, data, position)
            holder.itemView.setOnClickListener {
                itemOnClick?.invoke(data, position)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return createBinding(parent, viewType)
    }

    abstract fun createBinding(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    abstract fun bind(binding: RecyclerView.ViewHolder, data: T, position: Int)
}

class Comparator<T : Any> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(
        oldItem: T,
        newItem: T
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: T,
        newItem: T
    ): Boolean {
        return areItemsTheSame(oldItem, newItem)
    }

}