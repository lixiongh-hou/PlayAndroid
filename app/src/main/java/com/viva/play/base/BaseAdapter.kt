package com.viva.play.base

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * @author 李雄厚
 *
 *
 */
abstract class BaseAdapter<B : ViewDataBinding, T>(val data: MutableList<T>) :
    RecyclerView.Adapter<BaseViewHolder<B>>() {

    var clickEvent: ((T, B, Int) -> Unit)? = null


    @SuppressLint("NotifyDataSetChanged")
    fun refreshData(data: List<T>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    fun addData(data: List<T>) {
        this.data.addAll(data)
        notifyItemRangeInserted(itemCount - 1, data.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<B> {
        return BaseViewHolder(createBinding(parent, viewType))
    }

    override fun onBindViewHolder(holder: BaseViewHolder<B>, position: Int) {
        bind(holder.binding, data[position], position)
        holder.binding.executePendingBindings()
        holder.binding.root.setOnClickListener {
            clickEvent?.invoke(data[position], holder.binding, position)

        }
    }

    override fun getItemCount(): Int = data.size

    abstract fun createBinding(parent: ViewGroup, viewType: Int): B
    abstract fun bind(binding: B, data: T, position: Int)
}


open class BaseViewHolder<B : ViewDataBinding>(val binding: B) : RecyclerView.ViewHolder(binding.root)


class DiffCallback<T>(
    private val oldPlaylist: List<T>,
    private val newPlaylist: List<T>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldPlaylist.size

    override fun getNewListSize(): Int = newPlaylist.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldPlaylist[oldItemPosition] == newPlaylist[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return areItemsTheSame(oldItemPosition, newItemPosition)
    }

}