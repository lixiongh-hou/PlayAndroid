package com.viva.play.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.viva.play.base.BaseAdapter
import com.viva.play.databinding.ItemSearchHotBinding
import com.viva.play.db.entity.PoHotKeyEntity

/**
 * @author 李雄厚
 *
 *
 */
class HotAdapter(
    private val context: Context,
    data: MutableList<PoHotKeyEntity>
) :
    BaseAdapter<ItemSearchHotBinding, PoHotKeyEntity>(data) {
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemSearchHotBinding =
        ItemSearchHotBinding.inflate(LayoutInflater.from(context), parent, false)

    override fun bind(binding: ItemSearchHotBinding, data: PoHotKeyEntity, position: Int) {
        binding.tvKey.text = data.key
    }
}