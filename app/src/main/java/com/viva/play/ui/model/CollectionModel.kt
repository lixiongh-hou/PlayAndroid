package com.viva.play.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.viva.play.base.BaseModel
import com.viva.play.db.BaseDataBase
import com.viva.play.db.entity.PoCollectArticleEntity
import com.viva.play.di.MadeInCommon
import com.viva.play.paging.CollectionArticleMediator
import com.viva.play.service.CommonService
import com.viva.play.service.doFailure
import com.viva.play.service.doSuccess
import com.viva.play.service.request.CommonRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * @author 李雄厚
 *
 *
 */
@HiltViewModel
class CollectionModel @Inject constructor(
    private val commonRequest: CommonRequest,
    private val baseDataBase: BaseDataBase,
    @MadeInCommon
    private val commonService: CommonService
) : BaseModel() {

//    private fun getPagingData(): Flow<PagingData<PoCollectArticleEntity>> =
//        Pager(
//            config = PagingConfig(
//                pageSize = 20,
//                prefetchDistance = 2,
//                initialLoadSize = 20,
//                enablePlaceholders = false
//            ),
//            pagingSourceFactory = { CollectionArticleSource(commonRequest) }
//        ).flow
//
//    private val _removeItemFlow = MutableStateFlow(mutableListOf<Any>())
//    private val removedItemsFlow: Flow<MutableList<Any>> get() = _removeItemFlow
//    fun bindPaging(adapter: CollectionArticleAdapter) {
//        viewModelScope.launch {
//            getPagingData().cachedIn(viewModelScope)
//                .combine(removedItemsFlow) { pagingData, removed ->
//                    pagingData.filter {
//                        //过滤出没有的删除的数据
//                        it !in removed
//                    }
//                }.collectLatest {
//                    adapter.submitData(it)
//                }
//        }
//    }
//
//    fun remove(item: Any) {
//        val removes = _removeItemFlow.value
//        val list = mutableListOf(item)
//        list.addAll(removes)
//        _removeItemFlow.value = list
//    }

    @ExperimentalPagingApi
    val pagingData: Flow<PagingData<PoCollectArticleEntity>> =
        Pager(
            config = pagingConfig(),
            remoteMediator = CollectionArticleMediator(baseDataBase, commonService)
        ) {
            baseDataBase.collectDao().findArticle()
        }.flow.cachedIn(viewModelScope)

    /**
     * 这个id只代表我的页面收藏的文章id
     */
    var id: Int = -1

    /**
     * 这个id是代表站内文章id，如果要刷新首页使用这个id
     */
    var originId: Int = -1

    private val _unMyCollectArticle = MutableLiveData<Boolean>()
    val unMyCollectArticle: LiveData<Boolean>
        get() = _unMyCollectArticle

    fun unMyCollectArticle() {
        commonRequest.unMyCollectArticle(viewModelScope, id, originId) {
            it.doSuccess {
                _unMyCollectArticle.postValue(false)
            }
            it.doFailure { apiError ->
                error.postValue(apiError)
            }
        }
    }

}