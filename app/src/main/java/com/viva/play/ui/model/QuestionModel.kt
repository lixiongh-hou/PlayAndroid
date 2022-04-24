package com.viva.play.ui.model

import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.viva.play.base.BaseModel
import com.viva.play.db.BaseDataBase
import com.viva.play.db.entity.PoQuestionEntity
import com.viva.play.di.MadeInCommon
import com.viva.play.paging.QuestionMediator
import com.viva.play.service.CommonService
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
class QuestionMode @Inject constructor(
    commonRequest: CommonRequest,
    private val baseDataBase: BaseDataBase,
    @MadeInCommon
    private val commonService: CommonService
) : BaseModel(commonRequest) {

    var id: Int = -1

    @ExperimentalPagingApi
    val pagingData: Flow<PagingData<PoQuestionEntity>> =
        Pager(
            config = pagingConfig(),
            remoteMediator = QuestionMediator(baseDataBase, commonService)
        ) {
            baseDataBase.questionDao().findQuestion()
        }.flow.cachedIn(viewModelScope)
}
