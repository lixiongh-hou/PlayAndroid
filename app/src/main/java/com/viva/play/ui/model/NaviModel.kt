package com.viva.play.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.viva.play.base.BaseModel
import com.viva.play.service.doFailure
import com.viva.play.service.doSuccess
import com.viva.play.service.request.CommonRequest
import com.viva.play.ui.vo.VoChapterEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author 李雄厚
 *
 *
 */
@HiltViewModel
class NaviModel @Inject constructor(
    private val commonRequest: CommonRequest
) : BaseModel() {

    private val _naviList = MutableLiveData<List<VoChapterEntity>>()
    val naviList: LiveData<List<VoChapterEntity>>
        get() = _naviList

    fun getNaviList() {
        commonRequest.getNaviList(viewModelScope) {
            it.doSuccess { success ->
                _naviList.postValue(success)
            }
            it.doFailure { apiError ->
                error.postValue(apiError)
            }
        }
    }
}