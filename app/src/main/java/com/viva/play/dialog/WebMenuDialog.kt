package com.viva.play.dialog

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.fragment.app.FragmentManager
import com.viva.play.R
import com.viva.play.base.BaseFragmentDialog
import com.viva.play.databinding.DialogWebMenuBinding
import com.viva.play.ui.activity.SettingActivity
import com.viva.play.utils.CookieCache
import com.viva.play.utils.SettingUtils
import com.viva.play.utils.web.HostInterceptUtils
import com.viva.play.utils.web.HostInterceptUtils.TYPE_INTERCEPT_BLACK
import com.viva.play.utils.web.HostInterceptUtils.TYPE_NOTHING
import com.viva.play.utils.web.HostInterceptUtils.TYPE_ONLY_WHITE

/**
 * @author 李雄厚
 *
 *
 */
class WebMenuDialog : BaseFragmentDialog<DialogWebMenuBinding>() {


    companion object {
        private var listener: OnMenuClickListener? = null
        private const val URL = "url"
        private const val COLLECTED = "collected"
        private const val READ_LATER = "readLater"
        fun show(
            fm: FragmentManager,
            url: String,
            collected: Boolean,
            readLater: Boolean,
            listener: OnMenuClickListener
        ): WebMenuDialog {
            this.listener = listener
            return WebMenuDialog().apply {
                setAnimStyle(R.style.DialogBottomAnim)
                setGravity(Gravity.BOTTOM)
                arguments = Bundle().apply {
                    putString(URL, url)
                    putBoolean(COLLECTED, collected)
                    putBoolean(READ_LATER, readLater)
                }
                show(fm)
            }
        }
    }

    private val url by lazy {
        arguments?.getString(URL, "") ?: ""
    }
    private val collected by lazy {
        arguments?.getBoolean(COLLECTED, false) ?: false
    }
    private val readLater by lazy {
        arguments?.getBoolean(READ_LATER, false) ?: false
    }

    override fun convertView(binding: DialogWebMenuBinding) {
        binding.dialog = this
        var host = ""
        if (url.isNotEmpty()) {
            val uri = Uri.parse(url)
            host = uri.host.toString()
        }
        binding.data = WebMenu().apply {
            name = host
            readLater = this@WebMenuDialog.readLater
            collected = this@WebMenuDialog.collected
            interruptState = when (SettingUtils.urlInterceptType) {
                TYPE_NOTHING -> false
                TYPE_ONLY_WHITE -> true
                TYPE_INTERCEPT_BLACK -> true
                else -> false
            }
            interruptName = HostInterceptUtils.getName(SettingUtils.urlInterceptType)
        }
    }

    fun onHome() {
        listener?.onHome()
        dismiss()
    }

    fun onCollect(view: View) {
        if (CookieCache.doIfLogin(view.context)) {
            listener?.onCollect()
        }
        dismiss()
    }

    fun onReadLater(){
        listener?.onReadLater()
        dismiss()
    }

    fun onRefresh() {
        listener?.onRefresh()
        dismiss()
    }

    fun onGoTop() {
        listener?.onGoTop()
        dismiss()
    }

    fun urlInterceptType() {
        when (SettingUtils.urlInterceptType) {
            TYPE_NOTHING -> SettingUtils.urlInterceptType = TYPE_ONLY_WHITE
            TYPE_ONLY_WHITE -> SettingUtils.urlInterceptType = TYPE_INTERCEPT_BLACK
            TYPE_INTERCEPT_BLACK -> SettingUtils.urlInterceptType = TYPE_NOTHING
        }
        binding.data = binding.data!!.apply {
            interruptState = when (SettingUtils.urlInterceptType) {
                TYPE_NOTHING -> false
                TYPE_ONLY_WHITE -> true
                TYPE_INTERCEPT_BLACK -> true
                else -> false
            }
            interruptName = HostInterceptUtils.getName(SettingUtils.urlInterceptType)
        }
    }

    fun onCloseActivity() {
        listener?.onCloseActivity()
        dismiss()
    }

    fun setting() {
        requireContext().startActivity(Intent(requireContext(), SettingActivity::class.java))
        dismiss()
    }

    inner class WebMenu {
        var name: String = ""
        var readLater: Boolean = false
        var collected: Boolean = false
        var interruptState: Boolean = false
        var interruptName: String = ""

    }

    interface OnMenuClickListener {
        fun onShareArticle()
        fun onCollect()
        fun onReadLater()
        fun onHome()
        fun onRefresh()
        fun onGoTop()
        fun onCloseActivity()
        fun onShare()
    }
}