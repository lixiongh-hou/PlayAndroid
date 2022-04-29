package com.viva.play.ui.model

import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.viva.play.base.BaseModel
import com.viva.play.db.entity.PoCollectLinkEntity
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
class CollectionLinkModel @Inject constructor(
    private val commonRequest: CommonRequest
) : BaseModel() {

    val pagingData: Flow<PagingData<PoCollectLinkEntity>> =
        Pager(
            config = pagingConfig()
        ) {
            commonRequest.getCollectLinkList()
        }.flow.cachedIn(viewModelScope)


    var id = -1
    fun unCollectLink() {
        commonRequest.unCollectLink(viewModelScope, id) {
            it.doSuccess {
            }
            it.doFailure { apiError ->
                error.postValue(apiError)
            }
        }
    }

    fun editCollectLink(data: PoCollectLinkEntity) {
        commonRequest.editCollectLink(viewModelScope, data) {
            it.doSuccess {
            }
            it.doFailure { apiError ->
                error.postValue(apiError)
            }
        }
    }
}