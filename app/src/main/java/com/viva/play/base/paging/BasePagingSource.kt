package com.viva.play.base

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.annotations.SerializedName

/**
 * @author 李雄厚
 *
 *
 */
data class BaseSourceData<value>(
    /**
     * 这个后台接口获得,后面时候还要数据 true 后面没有数据 false 后面还有数据
     */
    val over: Boolean,
    @SerializedName("datas")
    val data: List<value>

)

abstract class BasePagingSource<value : Any> : PagingSource<Int, value>() {

    companion object {
        private const val PAGE = 0
    }

    override fun getRefreshKey(state: PagingState<Int, value>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, value> {
        return try {
            //这个key就是代表着当前的页数。注意key是可能为null的，如果为null的话，我们就默认将当前页数设置为第一页
            val page = params.key ?: PAGE
            val offset = page * 20
            val repoResponse = loadData(page, offset)
            val prevKey = if (page > PAGE) page - 1 else null
            val nextKey =
                if (repoResponse.data.isNotEmpty() && !repoResponse.over) page + 1 else null

            LoadResult.Page(repoResponse.data, prevKey, nextKey)

        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    /**
     * @param page 数据加载到第几页
     * @param offset 这个值用来本地数据库分页用的
     */
    abstract suspend fun loadData(page: Int, offset: Int): BaseSourceData<value>

}