package com.viva.play.dialog

import android.view.Gravity
import com.viva.play.R
import com.viva.play.base.BaseFragmentDialog
import com.viva.play.databinding.DialogLoginBinding

/**
 * @author 李雄厚
 *
 *
 */
class LoginDialog : BaseFragmentDialog<DialogLoginBinding>() {

    init {
        setAnimStyle(R.style.DialogCentreAnim)
        setGravity(Gravity.CENTER)
        setMargin(40)
        setOutCancel(false)
    }

    override fun convertView(binding: DialogLoginBinding) {

    }
}