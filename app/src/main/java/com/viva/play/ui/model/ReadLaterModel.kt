package com.viva.play.ui.model

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.viva.play.base.BaseModel
import com.viva.play.db.entity.PoCollectLinkEntity
import com.viva.play.db.entity.PoReadLaterEntity
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
class ReadLaterModel @Inject constructor(
    private val commonRequest: CommonRequest
) : BaseModel(commonRequest) {

    val pagingData: Flow<PagingData<PoReadLaterEntity>> =
        Pager(
            config = pagingConfig()
        ) {
            commonRequest.getReadLaterList()
        }.flow.cachedIn(viewModelScope)


    fun removeReadLaterAll() {
        commonRequest.removeReadLaterAll(viewModelScope) {

        }
    }

}