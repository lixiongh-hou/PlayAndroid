package com.viva.play.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.room.withTransaction
import com.viva.play.base.paging.BaseRemoteMediator
import com.viva.play.db.BaseDataBase
import com.viva.play.db.entity.PoQuestionEntity
import com.viva.play.service.CommonService

/**
 * @author 李雄厚
 *
 *
 */
@ExperimentalPagingApi
class QuestionMediator(
    private val baseDataBase: BaseDataBase,
    private val commonService: CommonService
) : BaseRemoteMediator<PoQuestionEntity>() {

    override suspend fun loadData(pageKey: Int?, loadType: LoadType): Boolean {
        //请求网络分页数据
        val page = pageKey ?: 0
        val result = commonService.getQuestionList(page).data
        //过滤掉id相等的重复数据
        val resultFilter = result!!.data.distinctBy{
            it.id
        }
        val items = PoQuestionEntity.parse(resultFilter, page)

        //插入数据库
        baseDataBase.withTransaction {
            if (loadType == LoadType.REFRESH) {
                baseDataBase.questionDao().delete()
            }
            baseDataBase.questionDao().insert(items)
        }
        return result.over
    }
}