package com.viva.play.dialog

import android.view.Gravity
import com.viva.play.R
import com.viva.play.base.BaseFragmentDialog
import com.viva.play.databinding.DialogTipBinding

/**
 * @author 李雄厚
 *
 *
 */
class TipDialog : BaseFragmentDialog<DialogTipBinding>() {
    var title: String = ""
    var msg: String = ""
    var yesText: String = "确定"
    var noText: String = "取消"
    var singleBtnYes: Boolean = false

    var callbackYes: (() -> Unit)? = null
    var callbackNo: (() -> Unit)? = null

    override fun convertView(binding: DialogTipBinding) {
        binding.dialog = this
        binding.dialogTipNo.setOnClickListener {
            callbackNo?.invoke()
            dismiss()
        }
        binding.dialogTipYes.setOnClickListener {
            callbackYes?.invoke()
            dismiss()
        }
    }

    class Builder {
        var title: String = ""
        var msg: String = ""
        var yesText: String = "确定"
        var noText: String = "取消"
        var singleBtnYes: Boolean = false

        var callbackYes: (() -> Unit)? = null
        var callbackNo: (() -> Unit)? = null

        fun builder(): TipDialog = TipDialog().apply {
            setAnimStyle(R.style.DialogCentreAnim)
            setGravity(Gravity.CENTER)
            setMargin(40)
            title = this@Builder.title
            msg = this@Builder.msg
            yesText = this@Builder.yesText
            noText = this@Builder.noText
            singleBtnYes = this@Builder.singleBtnYes
            callbackYes = this@Builder.callbackYes
            callbackNo = this@Builder.callbackNo
        }
    }
}