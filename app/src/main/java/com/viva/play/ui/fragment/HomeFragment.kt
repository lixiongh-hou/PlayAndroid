package com.viva.play.ui.fragment

import android.animation.Animator
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.viva.play.MainActivity
import com.viva.play.R
import com.viva.play.adapter.HomeArticleAdapter
import com.viva.play.base.BaseFragment
import com.viva.play.databinding.FragmentHomeBinding
import com.viva.play.db.entity.PoHomeArticleEntity
import com.viva.play.db.entity.PoReadLaterEntity
import com.viva.play.dialog.WebDialog
import com.viva.play.service.EventBus
import com.viva.play.ui.event.CollectionEvent
import com.viva.play.ui.model.HomeModel
import com.viva.play.utils.*
import com.viva.play.utils.ToastUtil.toast
import dagger.hilt.android.AndroidEntryPoint
import per.goweii.anylayer.Layer

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val mHomeModel by viewModels<HomeModel>()

    private lateinit var adapter: HomeArticleAdapter

    private var mWebDialog: WebDialog? = null
    private var readLaterData: PoHomeArticleEntity? = null

    private var bookFragment: BookFragment? = null
    private var onBackPressedCallback: OnBackPressedCallback? = null
    private var abcAnim: Animator? = null

    companion object {
        fun newInstance() = HomeFragment()
    }

    override fun initView(savedInstanceState: Bundle?) {
        pureScrollMode()
        initRefresh()
        adapter = HomeArticleAdapter(requireContext()) {
            adapter.setFooterType(HomeArticleAdapter.LOADING)
            mHomeModel.getArticle(page)
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.bindDivider()
        setupScrollListener()

        val fragment = childFragmentManager.findFragmentByTag("BookFragment")
        if (fragment == null) {
            bookFragment = BookFragment()
            val ft = childFragmentManager.beginTransaction()
            childFragmentManager.executePendingTransactions()
            ft.add(R.id.flDlSecondFloor, bookFragment!!, "BookFragment")
            ft.commitAllowingStateLoss()
        } else {
            bookFragment = fragment as BookFragment
        }
        onBackPressedCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                binding.dl.open(300)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback!!)
        val bottomBar = (requireActivity() as MainActivity).binding.llBottomBar
        binding.flDlSecondFloor.setPadding(0, 0, 0, binding.dl.getCloseHeight())
        var color = requireContext().getThemeColor(R.attr.colorMain)
        color = ColorUtils.changingColor(color, Color.BLACK, 0.5F)
        binding.ivSecondFloorBackground.setBackgroundColor(color)
        binding.vDlContentMask.setOnClickListener {
            binding.dl.open(300)
        }
        binding.dl.setEnable(true)
        binding.dl.setDraggable(false)
        binding.dl.onClosed {
            onBackPressedCallback?.isEnabled = true
            binding.vDlContentMask.isClickable = true
            binding.dl.setDraggable(true)
            adapter.banner?.isAutoPlay(false)
            adapter.banner?.stopAutoPlay()
        }
        binding.dl.onOpened {
            onBackPressedCallback?.isEnabled = false
            binding.vDlContentMask.isClickable = false
            binding.dl.setDraggable(false)
            if (bookFragment != null) {
                childFragmentManager.beginTransaction()
                    .hide(bookFragment!!)
                    .commitAllowingStateLoss()
            }
            adapter.banner?.isAutoPlay(true)
            adapter.banner?.startAutoPlay()
        }
        binding.dl.onDragStart {
            adapter.banner?.isAutoPlay(false)
            adapter.banner?.stopAutoPlay()
        }
        binding.dl.onDragEnd {
        }
        binding.dl.onDragging { f ->
            bottomBar.translationY = bottomBar.height * f
            binding.flDlSecondFloor.alpha = f
//            val minScale = 0.95F
//            val s = minScale + f * (1F - minScale)
//            binding.flDlSecondFloor.pivotX = binding.flDlSecondFloor.width * 0.5F
//            binding.flDlSecondFloor.pivotY = binding.flDlSecondFloor.height * 0F
//            binding.flDlSecondFloor.scaleX = s
//            binding.flDlSecondFloor.scaleY = s
            if (abcAnim == null || !abcAnim!!.isRunning) {
                binding.abc.translationY = -binding.abc.height * f
            }
            val fromFaction = 0.6F
            if (f < fromFaction) {
                binding.mSmartRefreshLayout.alpha = 1F
                binding.vDlContentMask.alpha = 0F
                binding.vDlContentHandle.alpha = 0F
                binding.ivSecondFloorBackground.alpha = 0F
            } else {
                val fa = (f - fromFaction) / (1 - fromFaction)
                binding.vDlContentMask.alpha = fa
                binding.vDlContentHandle.alpha = fa
                binding.ivSecondFloorBackground.alpha = fa
            }
            if (f == 1f) {
                binding.mSmartRefreshLayout.visibility = View.INVISIBLE
            } else {
                binding.mSmartRefreshLayout.visibility = View.VISIBLE
            }
        }
        binding.mSmartRefreshLayout.setOnMultiListener(object : SimpleOnMultiListener() {
            private var isSecondFloor = false
            override fun onHeaderMoving(
                header: RefreshHeader?,
                isDragging: Boolean,
                percent: Float,
                offset: Int,
                headerHeight: Int,
                maxDragHeight: Int
            ) {
                super.onHeaderMoving(
                    header,
                    isDragging,
                    percent,
                    offset,
                    headerHeight,
                    maxDragHeight
                )
                val secondPercent = 1.5f
                if (isDragging) {
                    if (percent > secondPercent) {
                        if (!isSecondFloor) {
                            isSecondFloor = true
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                binding.mSmartRefreshLayout.performHapticFeedback(
                                    HapticFeedbackConstants.GESTURE_START
                                )
                            } else {
                                binding.mSmartRefreshLayout.performHapticFeedback(
                                    HapticFeedbackConstants.VIRTUAL_KEY
                                )
                            }
                            abcAnim?.cancel()
                            abcAnim = ObjectAnimator.ofFloat(
                                binding.abc, "translationY",
                                binding.abc.translationY, -binding.abc.height.toFloat()
                            )
                            abcAnim?.start()
//                            binding.refreshHeader.setTextAndHideIcon("释放进入书籍教程")
                        }
                    } else {
                        if (isSecondFloor) {
                            isSecondFloor = false
                            abcAnim?.cancel()
                            abcAnim = ObjectAnimator.ofFloat(
                                binding.abc, "translationY",
                                binding.abc.translationY, 0f
                            )
                            abcAnim?.start()
//                            binding.refreshHeader.restoreToCurrState()
                        }
                    }
                }
            }

            override fun onHeaderReleased(
                header: RefreshHeader?,
                headerHeight: Int,
                maxDragHeight: Int
            ) {
                super.onHeaderReleased(header, headerHeight, maxDragHeight)
                if (isSecondFloor) {
                    isSecondFloor = false
                    abcAnim?.cancel()
                    abcAnim = ObjectAnimator.ofFloat(
                        binding.abc, "translationY",
                        binding.abc.translationY, -binding.abc.height.toFloat()
                    )
                    abcAnim?.duration = 300
                    abcAnim?.start()
                    binding.mSmartRefreshLayout.closeHeaderOrFooter()
                    binding.mSmartRefreshLayout.finishRefresh()
                    binding.dl.close(300)
                    if (bookFragment != null) {
                        childFragmentManager
                            .beginTransaction().show(bookFragment!!)
                            .commitAllowingStateLoss()
                    }
                }
            }

            override fun onStateChanged(
                refreshLayout: RefreshLayout,
                oldState: RefreshState,
                newState: RefreshState
            ) {
            }
        })
    }

    override fun initData() {
        mHomeModel.getHomeData(local = true)
        mHomeModel.homeData.observe(viewLifecycleOwner) {
            adapter.refreshData(it.getHomeData())
            successAfter(adapter.itemCount)
        }

        mHomeModel.article.observe(viewLifecycleOwner) {
            adapter.addData(it)
            mWebDialog?.notifyDataSetChanged()
            successAfter(adapter.itemCount)
        }

        mHomeModel.loadError.observe(viewLifecycleOwner) {
            adapter.setFooterType(HomeArticleAdapter.LOAD_FAILED)
            failureAfter()
            it.message.toast()
        }

        mHomeModel.isReadLater.observe(viewLifecycleOwner) {
            if (readLaterData == null) {
                mWebDialog?.switchIvReadLaterState(it)
            } else {
                if (it) {
                    //移除稍后阅读
                    mWebDialog?.switchIvReadLaterState(false)
                    mHomeModel.removeReadLater(readLaterData!!.link)
                } else {
                    //添加稍后阅读
                    mWebDialog?.switchIvReadLaterState(true)
                    mHomeModel.addReadLater(
                        PoReadLaterEntity(
                            link = readLaterData!!.link, title = readLaterData!!.title
                        )
                    )
                }
            }
            readLaterData = null

        }

        mHomeModel.error.observe(viewLifecycleOwner) {
            it.message.toast()
        }

        adapter.itemOnClick = { data, _ ->
            UrlOpenUtils.with(data).open(requireContext())
        }

        adapter.itemLongClick = { _, position ->
            showWebDialog(position)
            true
        }

        adapter.collectClick = { view, data ->
            if (CookieCache.doIfLogin(requireContext())) {
                if (data.collect) {
                    mHomeModel.unCollectArticle(data.id)
                } else {
                    mHomeModel.collectArticle(data.id)
                }
                data.collect = !data.collect
                view.setChecked(!view.isChecked, true)
                view.toggle()
            }
        }

        observeEvent(key = EventBus.COLLECTED) {
            it as CollectionEvent
            if (isDetached) {
                return@observeEvent
            }
            if (it.id == 0) {
                return@observeEvent
            }
            run b@{
                adapter.data.forEachIndexed { index, entity ->
                    if (entity.id == it.id) {
                        entity.collect = it.collect
                        adapter.notifyItemChanged(index)
                        return@b
                    }
                }
            }
        }
        observeEvent(key = EventBus.LOGIN) {
            startRefresh()
        }

    }

    override fun startRefresh() {
        super.startRefresh()
        mHomeModel.getHomeData(refresh = true)
    }

    override fun startLoadMore() {
        super.startLoadMore()
        mHomeModel.getArticle(page)
    }

    override fun onPause() {
        super.onPause()
        adapter.banner?.isAutoPlay(false)
        adapter.banner?.stopAutoPlay()
    }

    override fun resume() {
        super.resume()
        adapter.banner?.isAutoPlay(true)
        adapter.banner?.startAutoPlay()
    }

    private fun setupScrollListener() {
        val layoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                if (adapter.itemCount > totalItemCount) {
                    adapter.setShowFooter(false)
                    return
                }
                mHomeModel.listScrolled(visibleItemCount, lastVisibleItem, totalItemCount) {
                    startLoadMore()
                }
            }
        })
    }

    private fun showWebDialog(position: Int) {
        //第一个position是Banner，取数据时要-1
        mWebDialog =
            WebDialog.create(
                context = requireContext(),
                currPos = position - 1,
                urls = adapter.data,
                singleTipMode = false
            )
        mWebDialog?.onPageChanged = { _, data ->
            mHomeModel.isReadLater(data.link)

            var currItemPos = 0
            run b@{
                adapter.data.forEachIndexed { index, poArticleEntity ->
                    if (poArticleEntity.id == data.id) {
                        currItemPos = index
                        return@b
                    }
                }
            }
            if (currItemPos < 0) {
                currItemPos = 0
            }
            binding.recyclerView.smoothScrollToPosition(currItemPos)
        }

        mWebDialog?.collectClick = { view, data ->
            if (CookieCache.doIfLogin(requireContext())) {
                if (data.collect) {
                    mHomeModel.unCollectArticle(data.id)
                } else {
                    mHomeModel.collectArticle(data.id)
                }
                data.collect = !data.collect
                view.setChecked(!view.isChecked, true)
                view.toggle()
                run b@{
                    adapter.data.forEachIndexed { index, entity ->
                        if (entity.id == data.id) {
                            entity.collect = data.collect
                            adapter.notifyItemChanged(index)
                            return@b
                        }
                    }
                }

            }
        }

        mWebDialog?.readLater = {
            readLaterData = it
            mHomeModel.isReadLater(it.link)
        }
        mWebDialog?.aReadLater = {
            mHomeModel.isReadLater(it.link)
        }
        mWebDialog?.onDismissListener(object : Layer.OnDismissListener {
            override fun onDismissing(layer: Layer) {}

            override fun onDismissed(layer: Layer) {
                mWebDialog = null
            }
        })
        mWebDialog?.show()
    }

}