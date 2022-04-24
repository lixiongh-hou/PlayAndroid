package com.viva.play.dialog

import android.view.Gravity
import androidx.databinding.ObservableField
import com.viva.play.R
import com.viva.play.base.BaseFragmentDialog
import com.viva.play.databinding.DialogEditCollectLinkBinding

/**
 * @author 李雄厚
 *
 *
 */
class EditCollectLinkDialog : BaseFragmentDialog<DialogEditCollectLinkBinding>() {

    var name = ObservableField("")
    var link = ObservableField("")
    var confirmClick: ((name: String, link: String) -> Unit)? = null

    override fun convertView(binding: DialogEditCollectLinkBinding) {
        binding.dialog = this
        binding.editCollectLinkTvNo.setOnClickListener {
            dismiss()
        }
        binding.editCollectLinkTvYes.setOnClickListener {
            confirmClick?.invoke(name.get()!!, link.get()!!)
            dismiss()
        }
    }

    class Builder {
        var name: String = ""
        var link: String = ""
        private var confirmClick: ((name: String, link: String) -> Unit)? = null

        fun setOnConfirmClick(confirmClick: (name: String, link: String) -> Unit): Builder = apply {
            this.confirmClick = confirmClick
        }

        fun builder(): EditCollectLinkDialog = EditCollectLinkDialog().apply {
            setAnimStyle(R.style.DialogBottomAnim)
            setGravity(Gravity.BOTTOM)
            setMargin(16)

            this.name.set(this@Builder.name)
            this.link.set(this@Builder.link)
            this.confirmClick = this@Builder.confirmClick
        }
    }
}