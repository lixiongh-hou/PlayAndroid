package com.viva.play.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.viva.play.base.BaseAdapter
import com.viva.play.databinding.ItemBookBinding
import com.viva.play.service.entity.BookEntity

/**
 * @author 李雄厚
 *
 *
 */
class BookAdapter(private val context: Context, data: MutableList<BookEntity>) :
    BaseAdapter<ItemBookBinding, BookEntity>(data) {
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemBookBinding =
        ItemBookBinding.inflate(LayoutInflater.from(context), parent, false)

    override fun bind(binding: ItemBookBinding, data: BookEntity, position: Int) {
        binding.data = data
    }
}