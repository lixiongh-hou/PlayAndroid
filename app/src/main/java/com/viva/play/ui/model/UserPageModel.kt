package com.viva.play.ui.model

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.viva.play.base.BaseModel
import com.viva.play.db.BaseDataBase
import com.viva.play.db.entity.PoArticleEntity
import com.viva.play.di.MadeInCommon
import com.viva.play.paging.QuestionMediator
import com.viva.play.paging.UserPagePagingSource
import com.viva.play.service.CommonService
import com.viva.play.service.doFailure
import com.viva.play.service.doSuccess
import com.viva.play.service.entity.DataEntity
import com.viva.play.service.entity.UserPageEntity
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
class UserPageModel @Inject constructor(
    private val commonRequest: CommonRequest,
    private val baseDataBase: BaseDataBase,
    @MadeInCommon
    private val commonService: CommonService
) : BaseModel(commonRequest) {

    var id: Int = -1
    var userId: Int = -1

    val pagingData: Flow<PagingData<PoArticleEntity>> =
        Pager(
            config = pagingConfig(),
            pagingSourceFactory = { UserPagePagingSource(userId, commonService) }
        ).flow.cachedIn(viewModelScope)

    val userPage = ObservableField<UserPageEntity>()
    val _userPage = MutableLiveData<UserPageEntity>()

    fun getUserPage(page: Int) {
        commonRequest.getUserPage(viewModelScope, userId, page) {
            it.doSuccess { success ->
                userPage.set(success)
                _userPage.postValue(success)
            }
            it.doFailure { apiError ->
                error.postValue(apiError)
            }
        }
    }

}