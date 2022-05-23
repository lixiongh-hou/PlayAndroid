package com.viva.play.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.FragmentManager
import com.viva.play.R
import com.viva.play.base.BaseFragmentDialog
import com.viva.play.databinding.DialogScanBinding

/**
 * @author 李雄厚
 *
 *
 */
class ScanDialog : BaseFragmentDialog<DialogScanBinding>() {

    private var listener: OnMenuClickListener? = null

    companion object {
        private const val RESULT = "result"
        fun show(
            manager: FragmentManager?,
            result: String,
            listener: OnMenuClickListener
        ): ScanDialog =
            ScanDialog().apply {
                this.listener = listener
                setAnimStyle(R.style.DialogBottomAnim)
                setGravity(Gravity.BOTTOM)
                arguments = Bundle().apply {
                    putString(RESULT, result)
                }
                this.show(manager)
            }
    }

    private val result by lazy {
        arguments?.getString(RESULT) ?: ""
    }

    override fun convertView(binding: DialogScanBinding) {
        binding.tvUrl.text = result

        binding.llAccess.setOnClickListener {
            listener?.access(result)
        }

        binding.llCopy.setOnClickListener {
            listener?.copy(result)
        }

        binding.llSaveText.setOnClickListener {
            listener?.saveText(result)
        }

        binding.llShare.setOnClickListener {
            listener?.share(result)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener?.dismiss()
    }

    interface OnMenuClickListener {
        fun access(result: String)
        fun copy(result: String)
        fun saveText(result: String)
        fun share(result: String)
        fun dismiss()
    }
}