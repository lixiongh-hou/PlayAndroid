package com.viva.play.base.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.viva.play.utils.NetworkUtils

/**
 * @author 李雄厚
 *
 *
 */
@ExperimentalPagingApi
abstract class BaseRemoteMediator<Value : BasePagingData> : RemoteMediator<Int, Value>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Value>): MediatorResult {
        val pageKey = when (loadType) {
            //首次访问 或者调用 PagingDataAdapter.refresh()时
            LoadType.REFRESH -> null
            //在当前加载的数据集的开头加载数据时
            LoadType.PREPEND -> {
                return MediatorResult.Success(endOfPaginationReached = true)
            }
            //下拉加载更多时
            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull()
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
                lastItem.page
            }
        }

        if (noNetworkLoadingLocally()) {
            return MediatorResult.Success(endOfPaginationReached = true)
        }

        return MediatorResult.Success(endOfPaginationReached = loadData(pageKey, loadType))
    }

    /**
     * 没有网络是否加载本地
     * @return 没有网络 true 有网络false
     */
    open fun noNetworkLoadingLocally(): Boolean {
        return !NetworkUtils.isNetworkAvailable()
    }

    abstract suspend fun loadData(pageKey: Int?, loadType: LoadType): Boolean
}