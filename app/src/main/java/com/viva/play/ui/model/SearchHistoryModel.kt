package com.viva.play.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.viva.play.base.BaseModel
import com.viva.play.db.entity.PoHotKeyEntity
import com.viva.play.db.entity.PoSearchHistoryEntity
import com.viva.play.service.*
import com.viva.play.service.DataConvert.convertNetworkError
import com.viva.play.service.entity.HotKeyEntity
import com.viva.play.service.request.CommonRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * @author 李雄厚
 *
 *
 */
@HiltViewModel
class SearchHistoryModel @Inject constructor(
    private val commonRequest: CommonRequest
) : BaseModel() {

    private val _hotKeyList = MutableLiveData<List<HotKeyEntity>>()
    val hotKeyList: LiveData<List<HotKeyEntity>>
        get() = _hotKeyList

    fun getHotKeyList() {
        commonRequest.getHotKeyList(viewModelScope) {
            it.doSuccess { success ->
                _hotKeyList.postValue(success)
            }
            it.doFailure { apiError ->
                error.postValue(apiError)
            }
        }
    }

    private val _uiState =
        MutableSharedFlow<List<PoHotKeyEntity>>()

    //使用Flow来监听，替换LiveData
    val uiState = _uiState.asSharedFlow()

    fun getHotKeyList1() {
        viewModelScope.launch {
            commonRequest.getHotKeyList()
                .flowOn(Dispatchers.Default)
                .catch { e ->
                    if (e is ServerException) {
                        error.postValue(ApiError(e.code, e.message ?: ""))
                    } else {
                        e.convertNetworkError {
                            error.postValue(it)
                        }
                    }
                }.collect {
                    _uiState.emit(it)
                }
        }
    }


    fun saveSearchHistory(key: String) {
        val data = PoSearchHistoryEntity(key, Date())
        commonRequest.saveSearchHistory(viewModelScope, data) {

        }
    }

    private val _historyList = MutableLiveData<List<PoSearchHistoryEntity>>()
    val historyList: LiveData<List<PoSearchHistoryEntity>>
        get() = _historyList

    fun getSearchHistory() {
        commonRequest.getSearchHistory(viewModelScope) {
            it.doSuccess { success ->
                _historyList.postValue(success)
            }
            it.doFailure { apiError ->
                error.postValue(apiError)
            }
        }
    }

    fun delHistory(data: PoSearchHistoryEntity) {
        commonRequest.delHistory(viewModelScope, data) {

        }
    }


    fun delAllHistory(){
        commonRequest.delAllHistory(viewModelScope){

        }
    }
}