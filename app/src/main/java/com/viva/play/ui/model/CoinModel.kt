package com.viva.play.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.viva.play.base.BaseModel
import com.viva.play.db.entity.PoReadRecordEntity
import com.viva.play.paging.CoinPagingSource
import com.viva.play.service.doFailure
import com.viva.play.service.doSuccess
import com.viva.play.service.entity.CoinRecord
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
class CoinModel @Inject constructor(
    private val commonRequest: CommonRequest
) : BaseModel() {

    val pagingData: Flow<PagingData<CoinRecord>> =
        Pager(
            config = pagingConfig(),
            pagingSourceFactory = { CoinPagingSource(commonRequest) }
        ).flow.cachedIn(viewModelScope)

    private val _coin = MutableLiveData<Int>()
    val coin: LiveData<Int>
        get() = _coin

    fun getCoin() {
        commonRequest.getCoin(viewModelScope) {
            it.doSuccess { success ->
                _coin.postValue(success)

            }
            it.doFailure { apiError ->
                error.postValue(apiError)
            }
        }
    }
}