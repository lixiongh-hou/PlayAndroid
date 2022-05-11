package com.viva.play.ui.model

import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.viva.play.base.BaseModel
import com.viva.play.db.BaseDataBase
import com.viva.play.db.entity.PoBookDetailsEntity
import com.viva.play.di.MadeInCommon
import com.viva.play.paging.BookDetailsMediator
import com.viva.play.service.CommonService
import com.viva.play.service.Url
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * @author 李雄厚
 *
 *
 */
@HiltViewModel
class BookDetailsModel @Inject constructor(
    private val baseDataBase: BaseDataBase,
    @MadeInCommon
    private val commonService: CommonService
) : BaseModel() {

    @ExperimentalPagingApi
    fun pagingData(cid: Int): Flow<PagingData<PoBookDetailsEntity>> =
        Pager(
            config = pagingConfig(),
            remoteMediator = BookDetailsMediator(baseDataBase, commonService, cid)
        ) {
            baseDataBase.bookDetailsDao()
                .findBookDetails(Url.ChapterArticle.replace("{page}", cid.toString()))
        }.flow.cachedIn(viewModelScope)
}