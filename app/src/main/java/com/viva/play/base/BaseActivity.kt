package com.viva.play.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.LoadState
import androidx.viewbinding.ViewBinding
import com.kennyc.view.MultiStateView
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.viva.play.R
import com.viva.play.base.paging.BasePagingDataAdapter
import com.viva.play.utils.*
import com.viva.play.utils.ToastUtil.toast

/**
 * @author 李雄厚
 *
 *
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        onCreateStart(savedInstanceState)
        super.onCreate(savedInstanceState)
        initView(savedInstanceState)
    }

    open fun onCreateStart(savedInstanceState: Bundle?) {}
    abstract fun initView(savedInstanceState: Bundle?)


    /*----------------------------------设置刷新控件---------------------------------------------------*/
    /**刷新,加载 */
    companion object {
        private const val REFRESH = 1
        private const val LOADING = 2
    }

    /**一页的数据数 */
    private var pageSize = 20

    /**页数 */
    var page = 0

    /**数据长度 */
    var dataLength = 0

    /**记录当前操作,刷新还是加载 */
    private var what: Int = REFRESH

    /**刷新布局*/
    private var mSmartRefreshLayout: SmartRefreshLayout? = null

    fun isRefresh(): Boolean {
        return what == REFRESH
    }

    /**
     * 开始刷新
     */
    open fun startRefresh() {
        what = REFRESH
        page = 0
    }

    /**
     * 开始加载
     */
    open fun startLoadMore() {
        what = LOADING
        page++
    }

    /**
     * @param isLoading 只加载本地数据就不用加载中页面
     */
    protected fun BasePagingDataAdapter<*>.bindLoadState(
        msv: MultiStateView,
        isLoading: Boolean = true,
        isMediator: Boolean = true
    ) {
        this.addLoadStateListener {
            if (!isMediator) {
                if (it.refresh is LoadState.NotLoading && this.itemCount == 0) {
                    msv.toEmpty {
                        msv.toLoading()
                        this.refresh()
                    }
                } else {
                    msv.toContent()
                }

                if (it.source.refresh is LoadState.NotLoading) {
                    msv.toContent()
                    successAfter(this.itemCount)
                }

                if (it.source.refresh is LoadState.Loading) {
                    if (NetworkUtils.isNetworkAvailable()) {
                        if (this.itemCount == 0) {
                            msv.toLoading()
                        }
                    }
                }
                if (it.source.refresh is LoadState.Error) {
                    if (this.itemCount == 0) {
                        msv.toError {
                            msv.toLoading()
                            this.refresh()
                        }
                    }
                    failureAfter()
                }
                // Toast任何错误，无论它是来自RemoteMediator还是PagingSource
                val errorState = it.source.append as? LoadState.Error
                    ?: it.source.prepend as? LoadState.Error
                    ?: it.source.refresh as? LoadState.Error
                    ?: it.append as? LoadState.Error
                    ?: it.prepend as? LoadState.Error
                errorState?.let { error ->
                    error.error.message?.let { msg ->
                        toast(msg)
                    }
                }

            } else {
                if (it.refresh is LoadState.NotLoading && this.itemCount == 0) {
                    msv.toEmpty {
                        msv.toLoading()
                        this.refresh()
                    }
                } else {
                    msv.toContent()
                }
                if (it.mediator?.refresh is LoadState.NotLoading) {
                    msv.toContent()
                    successAfter(this.itemCount)
                }
                if (it.mediator?.refresh is LoadState.Loading) {
                    if (NetworkUtils.isNetworkAvailable()) {
                        if (this.itemCount == 0) {
                            if (isLoading) {
                                msv.toLoading()
                            }
                        }
                    }
                }
                if (it.mediator?.refresh is LoadState.Error) {
                    if (this.itemCount == 0) {
                        msv.toError {
                            msv.toLoading()
                            this.refresh()
                        }
                    }
                    failureAfter()
                }
                // Toast任何错误，无论它是来自RemoteMediator还是PagingSource
                val errorState = it.source.append as? LoadState.Error
                    ?: it.source.prepend as? LoadState.Error
                    ?: it.append as? LoadState.Error
                    ?: it.prepend as? LoadState.Error
                    ?: it.mediator?.refresh as? LoadState.Error
                errorState?.let { error ->
                    error.error.message?.let { msg ->
                        toast(msg)
                    }
                }
            }
        }
    }

    /**
     * 初始化刷新控件
     */
    fun initRefresh(binding: ViewBinding) {
        if (mSmartRefreshLayout == null) {
            mSmartRefreshLayout = binding.root.findViewById(R.id.mSmartRefreshLayout)
        }
        mSmartRefreshLayout?.apply {
            setEnableAutoLoadMore(false)
            setEnableOverScrollBounce(true)
            setEnablePureScrollMode(false)
            setEnableRefresh(true)
            setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    // 开始加载
                    startLoadMore()
                }

                override fun onRefresh(refreshLayout: RefreshLayout) {
                    // 开始刷新
                    startRefresh()
                }
            })
        }
    }

    fun pureScrollMode(binding: ViewBinding) {
        if (mSmartRefreshLayout == null) {
            mSmartRefreshLayout = binding.root.findViewById(R.id.mSmartRefreshLayout)
        }
        mSmartRefreshLayout?.apply {
            setEnableRefresh(false)
            setEnableLoadMore(false)
            setEnablePureScrollMode(true)
            setEnableNestedScroll(true)
            setEnableOverScrollDrag(true)
        }
    }

    fun successAfter(newLength: Int) {
        if (!NetworkUtils.isNetworkAvailable()) {
            "网络连接失败".toast()
        }
//        Log.e("TAG", "newLength$newLength---dataLength$dataLength")
        mSmartRefreshLayout?.apply {
            if (what == LOADING) {
                if (newLength - dataLength < pageSize || newLength < pageSize) {
                    // 加载成功,没有有更多数据
//                    Log.e("TAG", "加载成功,没有有更多数据")
                    finishLoadMore(0, true, true)
                } else {
//                    Log.e("TAG", "加载成功,有更多数据")
                    // 加载成功,有更多数据
                    finishLoadMore(0, true, false)
                }
            } else {
                // 刷新成功、本来老版本后面参数有个0
                finishRefresh(true)
                if (newLength < pageSize) {
                    //刷新成功没有更多数据
                    setNoMoreData(true)
                } else {
                    setNoMoreData(false)
                }
            }
        }

        dataLength = newLength
    }

    /**
     * 数据请求失败后执行
     *
     */
    fun failureAfter() {
        mSmartRefreshLayout?.apply {
            if (what == LOADING) {
                // 加载失败
                finishLoadMore(0, false, false)
                page--
            } else {
                // 刷新失败、少个0
                finishRefresh(false)
            }
        }
    }

    /**
     * 启用刷新
     */
    protected fun setEnableRefresh(enable: Boolean = true) {
        mSmartRefreshLayout?.apply {
            setEnableRefresh(enable)
            setEnableOverScrollDrag(true)
        }
    }

    /**
     * 启用加载
     */
    protected fun setEnableLoadMore(enable: Boolean = true) {
        mSmartRefreshLayout?.apply {
            setEnableLoadMore(enable)
            setEnableOverScrollDrag(true)
            setEnableAutoLoadMore(true)
        }
    }
}