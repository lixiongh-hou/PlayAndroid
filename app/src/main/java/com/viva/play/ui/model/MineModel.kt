package com.viva.play.ui.model

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.viva.play.base.BaseModel
import com.viva.play.service.doFailure
import com.viva.play.service.doSuccess
import com.viva.play.service.entity.CoinRecordEntity
import com.viva.play.service.request.CommonRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author 李雄厚
 *
 *
 */
@HiltViewModel
class MineModel @Inject constructor(
    private val commonRequest: CommonRequest
) : BaseModel() {

    private val _coinRecordInfo = MutableLiveData<CoinRecordEntity>()
    val coinRecordInfo: LiveData<CoinRecordEntity>
        get() = _coinRecordInfo

    val name = ObservableField<String>()
    val coinCount = ObservableField<String>()
    val rank = ObservableField<String>()

    fun getCoinRecordInfo() {
        commonRequest.getCoinRecordInfo(viewModelScope) {
            it.doSuccess { success ->
                _coinRecordInfo.postValue(success)
            }
            it.doFailure { apiError ->
                error.postValue(apiError)
            }
        }
    }

    fun getUserInfo() {
        commonRequest.getUserInfo(viewModelScope) {
            it.doSuccess { success ->
                name.set(success.nickname)
                coinCount.set(success.coinCount.toString())
                rank.set(success.rank.toString())
            }
            it.doFailure { apiError ->
                error.postValue(apiError)
            }

        }
    }
}