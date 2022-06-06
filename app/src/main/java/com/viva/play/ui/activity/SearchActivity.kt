package com.viva.play.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import com.viva.play.R
import com.viva.play.base.BaseActivity
import com.viva.play.databinding.ActivitySearchBinding
import com.viva.play.ui.fragment.SearchHistoryFragment
import com.viva.play.ui.fragment.SearchResultFragment
import com.viva.play.utils.*
import com.viva.play.utils.bind.binding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : BaseActivity() {

    private val binding by binding<ActivitySearchBinding>()
    private var mSearchHistoryFragment: SearchHistoryFragment? = null
    private var mSearchResultFragment: SearchResultFragment? = null
    private lateinit var mFragmentManager: FragmentManager

    private var mIsResultPage = false

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, SearchActivity::class.java))
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.aivBack.setOnClickListener {
            if (mIsResultPage) {
                showHistoryFragment()
                return@setOnClickListener
            }
            finish()
        }
        binding.aivSearch.setOnClickListener {
            val key: String = binding.etSearch.text.toString()
            search(key)
        }

        binding.aivClear.setOnClickListener {
            binding.etSearch.setText("")
            hideSoftKeyboard()
            binding.etSearch.clearFocus()
            showHistoryFragment()
        }
        binding.etSearch.addTextChangedListener {
            val key = binding.etSearch.text.toString()
            if (key.isEmpty()) {
                binding.aivClear.isInvisible = true
            } else {
                binding.aivClear.isVisible = true
            }
        }
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val key = binding.etSearch.text.toString()
                search(key)
                return@setOnEditorActionListener true
            }
            false
        }

        mFragmentManager = supportFragmentManager
        val transaction = mFragmentManager.beginTransaction()
        mSearchHistoryFragment = SearchHistoryFragment.create()
        transaction.replace(R.id.fl, mSearchHistoryFragment!!)
            .commitAllowingStateLoss()

//        var posBottom: Int
//        var maxBottom = 0
//        addKeyBordHeightChangeCallBack(binding.root) {
//            posBottom = it
//            maxBottom = maxBottom.coerceAtLeast(posBottom)
//            LogUtil.e("posBottom$posBottom")
//            LogUtil.e("maxBottom$maxBottom")
//        }
    }

    override fun onBackPressed() {
        if (mIsResultPage) {
            showHistoryFragment()
            return
        }
        super.onBackPressed()
    }

    fun search(key: String) {
        hideSoftKeyboard()
        binding.etSearch.clearFocus()
        if (key.isEmpty()) {
            showHistoryFragment()
        } else {
            binding.etSearch.setText(key)
            binding.etSearch.setSelection(binding.etSearch.text.toString().length)
            showResultFragment(key)
            mSearchHistoryFragment?.addHistory(key)
        }
    }

    /**
     * 显示历史搜索界面
     */
    private fun showHistoryFragment() {
        if (!mIsResultPage) return
        mIsResultPage = false
        val transaction = mFragmentManager.beginTransaction()
        mSearchHistoryFragment = SearchHistoryFragment.create()
        transaction.replace(R.id.fl, mSearchHistoryFragment!!).commitAllowingStateLoss()
    }

    /**
     * 显示结果搜索界面
     */
    private fun showResultFragment(key: String) {
        if (mIsResultPage) return
        mIsResultPage = true
        val transaction = mFragmentManager.beginTransaction()
        mSearchResultFragment = SearchResultFragment.create(key)
        transaction.replace(R.id.fl, mSearchResultFragment!!).commitAllowingStateLoss()

    }

}