package com.viva.play.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.viva.play.base.BaseModel
import com.viva.play.service.doFailure
import com.viva.play.service.doSuccess
import com.viva.play.service.entity.ChapterEntity
import com.viva.play.service.request.CommonRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author 李雄厚
 *
 *
 */
@HiltViewModel
class KnowledgeModel @Inject constructor(
    private val commonRequest: CommonRequest
) : BaseModel() {

    private val _knowledgeList = MutableLiveData<List<ChapterEntity>>()
    val knowledgeList: LiveData<List<ChapterEntity>>
        get() = _knowledgeList

    fun getKnowledgeList() {
        commonRequest.getKnowledgeList(viewModelScope) {
            it.doSuccess { success ->
                _knowledgeList.postValue(success)
            }
            it.doFailure { apiError ->
                error.postValue(apiError)
            }
        }
    }
}