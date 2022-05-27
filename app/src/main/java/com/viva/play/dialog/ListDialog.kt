package com.viva.play.dialog

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.viva.play.R
import com.viva.play.base.BaseAdapter
import com.viva.play.base.BaseFragmentDialog
import com.viva.play.databinding.DialogListBinding
import com.viva.play.databinding.ItemDialogListBinding
import com.viva.play.utils.getThemeColor

/**
 * @author 李雄厚
 *
 *
 */
class ListDialog : BaseFragmentDialog<DialogListBinding>() {

    var title: String = ""
    var yesText: String = "确定"
    var noText: String = "取消"
    var singleBtnYes: Boolean = false
    private var itemClick: ((data: String, position: Int) -> Unit)? = null
    private var data = mutableListOf<String>()

    private var currSelectPos = -1
    private val adapter by lazy { Adapter(mutableListOf()) }

    @SuppressLint("NotifyDataSetChanged")
    override fun convertView(binding: DialogListBinding) {
        binding.dialog = this
        binding.basicUiRvDialogList.adapter = adapter
        adapter.refreshData(data)

        adapter.clickEvent = { _, _, position ->
            currSelectPos = position
            adapter.notifyDataSetChanged()

        }
        binding.dialogListYes.setOnClickListener {
            itemClick?.invoke(data[currSelectPos], currSelectPos)
            dismiss()
        }
        binding.dialogListNo.setOnClickListener {
            dismiss()
        }
    }

    inner class Adapter(data: MutableList<String>) :
        BaseAdapter<ItemDialogListBinding, String>(data) {
        override fun createBinding(parent: ViewGroup, viewType: Int): ItemDialogListBinding =
            ItemDialogListBinding.inflate(LayoutInflater.from(mContext), parent, false)

        override fun bind(binding: ItemDialogListBinding, data: String, position: Int) {
            binding.data = data
            if (position == currSelectPos) {
                binding.tvDialogListName.setTextColor(mContext!!.getThemeColor(R.attr.colorTextMain))
            } else {
                binding.tvDialogListName.setTextColor(mContext!!.getThemeColor(R.attr.colorTextSurface))
            }
        }
    }

    class Builder {
        var title: String = "标题"
        var yesText: String = "确定"
        var noText: String = "取消"
        var singleBtnYes: Boolean = false

        var itemClick: ((data: String, position: Int) -> Unit)? = null
        private var data = mutableListOf<String>()
        var currSelectPos = -1

        fun setData(vararg data: String): Builder = apply {
            this.data.addAll(data)
        }

        fun builder(): ListDialog = ListDialog().apply {
            setAnimStyle(R.style.DialogBottomAnim)
            setGravity(Gravity.BOTTOM)
            title = this@Builder.title
            yesText =  this@Builder.yesText
            noText = this@Builder.noText
            singleBtnYes = this@Builder.singleBtnYes
            itemClick =  this@Builder.itemClick
            data.addAll(this@Builder.data)
            currSelectPos = this@Builder.currSelectPos
        }
    }
}