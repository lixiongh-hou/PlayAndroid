package com.viva.play.ui.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import com.viva.play.R
import com.viva.play.base.BaseActivity
import com.viva.play.databinding.ActivityWebBinding
import com.viva.play.db.entity.PoCollectLinkEntity
import com.viva.play.db.entity.PoReadLaterEntity
import com.viva.play.dialog.WebMenuDialog
import com.viva.play.service.EventBus
import com.viva.play.ui.event.CollectionEvent
import com.viva.play.ui.model.WebModel
import com.viva.play.utils.*
import com.viva.play.utils.ToastUtil.toast
import com.viva.play.utils.bind.binding
import com.viva.play.utils.web.WebHolder
import com.viva.play.views.CollectView
import dagger.hilt.android.AndroidEntryPoint
import per.goweii.swipeback.SwipeBackAbility.OnlyEdge

@AndroidEntryPoint
class WebActivity : BaseActivity(), OnlyEdge {

    companion object {
        private const val URL = "url"
        private const val TITLE = "title"
        private const val ID = "id"
        private const val COLLECTED = "collected"
        private const val COLLECT_ID = "collectId"

        fun start(
            context: Context,
            url: String,
            title: String,
            id: Int,
            collected: Boolean,
            collectId: Int
        ) {
            context.startActivity(Intent(context, WebActivity::class.java).apply {
                putExtra(URL, url)
                putExtra(TITLE, title)
                putExtra(ID, id)
                putExtra(COLLECTED, collected)
                putExtra(COLLECT_ID, collectId)
            })
        }
    }

    private val binding by binding<ActivityWebBinding>()
    private val model by viewModels<WebModel>()
    private lateinit var mWebHolder: WebHolder

    private lateinit var etTitle: AppCompatEditText
    private lateinit var ivInto: AppCompatImageView
    private lateinit var cvCollect: CollectView

    private val url by lazy {
        intent.getStringExtra(URL) ?: ""
    }
    private val title by lazy {
        intent.getStringExtra(TITLE) ?: ""
    }
    private val id by lazy {
        intent.getIntExtra(ID, -1)
    }
    private val collected by lazy {
        intent.getBooleanExtra(COLLECTED, false)
    }
    private val collectId by lazy {
        intent.getIntExtra(COLLECT_ID, -1)
    }

    override fun initView(savedInstanceState: Bundle?) {
        if (collected) {
            model.addCollected(url)
            if (collectId != -1) {
                model.addCollectId(PoCollectLinkEntity(url = url, collectId = collectId))
            }
        }
        model.id.set(id)
        model.title.set(title)

        etTitle = binding.root.findViewById(R.id.etTitle)
        ivInto = binding.root.findViewById(R.id.ivInto)
        cvCollect = binding.root.findViewById(R.id.cvCollect)

        binding.model = model
        mWebHolder = WebHolder.with(this, url, binding.wc)
            .setOnPageTitleCallback {
                //这里每次切换网址都会回调
                setTitle()
                //TODO 添加阅读记录本地数据
            }
            .setOnPageLoadCallback(object : WebHolder.OnPageLoadCallback {
                override fun onPageStarted() {
                    etTitle.clearFocus()
                }

                override fun onPageFinished() {
                    //TODO  showWebGuideDialogIfNeeded()
                }

            })
            .setOnHistoryUpdateCallback {
                //这里每次切换网址都会回调
                updateCollect()
                if (mWebHolder.canGoBack()) {
                    binding.ivBack.setImageResource(R.drawable.ic_back)
                } else {
                    binding.ivBack.setImageResource(R.drawable.ic_close)
                }
                switchIconEnable(binding.ivForward, mWebHolder.canGoForward())
            }
            .setOnPageProgressCallback(object : WebHolder.OnPageProgressCallback {
                override fun onShowProgress() {}
                override fun onProgressChanged(progress: Int) {}

                override fun onHideProgress() {
                    //TODO  presenter.isAddedReadLater(mWebHolder.getUrl())
                }

            })
        mWebHolder.loadUrl(url)
        mWebHolder.setOnPageScrollEndListener {
            if (isReadLater()) {
                model.removeReadLater(mWebHolder.getUrl())
            }
        }

        binding.ivBack.setOnClickListener {
            if (mWebHolder.canGoBack()) {
                mWebHolder.goBack()
            } else {
                finish()
            }
        }
        binding.ivForward.setOnClickListener {
            if (mWebHolder.canGoForward()) {
                mWebHolder.goForward()
            }
        }

        binding.ivMenu.setOnClickListener {
            showMenuDialog()
        }

        binding.wc.setOnTouchDownListener {
            etTitle.clearFocus()

        }

        binding.wc.setOnDoubleClickListener { _, _ ->
            if (CookieCache.doIfLogin(this)) {
                if (!model.collected.get()) {
                    model.collect(mWebHolder)
                }
            }
            updateCollect()

        }

        ivInto.setOnClickListener {
            val url = model.title.get()
            if (url.isNullOrEmpty()) {
                val uri = Uri.parse(url)
                if (uri.scheme == "http" || uri.scheme == "https") {
                    mWebHolder.loadUrl(url)
                }
            }
            etTitle.clearFocus()
        }
        etTitle.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                ivInto.performClick()
                return@OnEditorActionListener true
            }
            false
        })
        etTitle.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                model.title.set(mWebHolder.getUrl())
                InputMethodUtils.show(etTitle)
                //TODO showQuickDialog()
            } else {
                setTitle()
                InputMethodUtils.hide(etTitle)
                //TODO  dismissQuickDialog()
            }
        }
        cvCollect.onClick = {
            if (CookieCache.doIfLogin(this)) {
                if (model.collected.get()) {
                    model.unCollect(mWebHolder)
                } else {
                    model.collect(mWebHolder)
                }
            }
            updateCollect()
        }
    }

    private fun setTitle() {
        if (mWebHolder.getTitle().isNotEmpty()) {
            model.title.set(mWebHolder.getTitle())
        } else {
            model.title.set(mWebHolder.getUrl())
        }
    }

    private fun isCollect(): Boolean {
        model.findCollected(mWebHolder.getUrl()) ?: return false
        return true
    }

    private fun isReadLater(): Boolean {
        model.findReadLater(mWebHolder.getUrl()) ?: return false
        return true
    }

    private fun switchIconEnable(view: View, enable: Boolean) {
        if (enable) {
            view.isEnabled = true
            view.alpha = 1.0f
        } else {
            view.isEnabled = false
            view.alpha = 0.382f
        }
    }


    override fun onPause() {
        mWebHolder.onPause()
        super.onPause()
    }

    override fun onResume() {
        mWebHolder.onResume()
        super.onResume()
        model.getCollectLinkList()
        model.getReadLaterListData().observe(this) {
            model.mReadLaterList.clear()
            it.forEach { entity ->
                model.mReadLaterList.add(entity.link)
            }

        }
        model.collectLinkList.observe(this) {
            updateCollect()
        }

        //收藏和取消收藏站内文章回调
        model.collectArticle.observe(this) {
            //通知首页刷新
            postValue(EventBus.COLLECTED, CollectionEvent(it, model.id.get()))
        }
        //收藏和删除网址回调
        model.collectLink.observe(this) {

        }
        //稍后阅读回调
        model.addReadLater.observe(this) {
            if (it) {
                "已加入我的书签".toast()
            } else {
                "已移出我的书签".toast()
            }
        }

        model.error.observe(this) {
            updateCollect()
            it.message.toast()

        }
    }

    private fun updateCollect() {
        model.collected.set(isCollect())
    }

    override fun onDestroy() {
        mWebHolder.onDestroy(true)
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (mWebHolder.handleKeyEvent(keyCode)) {
            true
        } else super.onKeyDown(keyCode, event)
    }

    override fun swipeBackOnlyEdge(): Boolean {
        return true
    }

    private fun showMenuDialog() {
        WebMenuDialog.show(
            supportFragmentManager,
            mWebHolder.getUrl(),
            model.collected.get(),
            isReadLater(),
            object : WebMenuDialog.OnMenuClickListener {
                override fun onShareArticle() {
                }

                override fun onCollect() {
                    if (isCollect()) {
                        model.unCollect(mWebHolder)
                    } else {
                        model.collect(mWebHolder)
                    }
                }

                override fun onReadLater() {
                    if (isReadLater()) {
                        model.removeReadLater(mWebHolder.getUrl())
                    } else {
                        model.addReadLater(
                            PoReadLaterEntity(
                                mWebHolder.getUrl(), model.title.get()!!
                            )
                        )
                    }
                }

                override fun onHome() {
                    var step = 0
                    while (mWebHolder.canGoBackOrForward(step - 1)) step--
                    mWebHolder.goBackOrForward(step)
                }

                override fun onRefresh() {
                    mWebHolder.reload()
                }

                override fun onGoTop() {
                    mWebHolder.goTop()
                }

                override fun onCloseActivity() {
                    finish()
                }

                override fun onShare() {
                }

            }
        )
    }
}
