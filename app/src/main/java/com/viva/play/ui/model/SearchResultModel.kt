package com.viva.play.ui.model

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.viva.play.base.BaseModel
import com.viva.play.db.entity.PoArticleEntity
import com.viva.play.paging.SearchResultPagingSource
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
class SearchResultModel @Inject constructor(
    private val commonRequest: CommonRequest,
) : BaseModel() {

    fun pagingData(key: String): Flow<PagingData<PoArticleEntity>> =
        Pager(
            config = pagingConfig(),
            pagingSourceFactory = { SearchResultPagingSource(key, commonRequest) }
        ).flow.cachedIn(viewModelScope)
}