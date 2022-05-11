package com.viva.play.ui.model

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.viva.play.base.BaseModel
import com.viva.play.db.entity.PoReadRecordEntity
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
class ReadRecordModel @Inject constructor(
    private val commonRequest: CommonRequest
) : BaseModel() {

    val pagingData: Flow<PagingData<PoReadRecordEntity>> =
        Pager(
            config = pagingConfig()
        ) {
            commonRequest.getReadRecordList()
        }.flow.cachedIn(viewModelScope)


    fun delReadRecord(data: PoReadRecordEntity) {
        commonRequest.delReadRecord(viewModelScope, data) {

        }
    }

    fun delAllReadRecord() {
        commonRequest.delAllReadRecord(viewModelScope) {

        }
    }
}