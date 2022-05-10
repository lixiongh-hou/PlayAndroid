package com.viva.play.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.room.withTransaction
import com.viva.play.base.paging.BaseRemoteMediator
import com.viva.play.db.BaseDataBase
import com.viva.play.db.entity.PoArticleEntity
import com.viva.play.service.CommonService
import com.viva.play.service.Url

/**
 * @author 李雄厚
 *
 *
 */
@ExperimentalPagingApi
class QuestionMediator(
    private val baseDataBase: BaseDataBase,
    private val commonService: CommonService
) : BaseRemoteMediator<PoArticleEntity>() {

    override suspend fun loadData(pageKey: Int?, loadType: LoadType): MediatorResult {

        //请求网络分页数据
        val page = pageKey ?: 0
        val result = commonService.getQuestionList(page).data
//        //过滤掉id相等的重复数据
//        val resultFilter = result!!.data.distinctBy{
//            it.id
//        }
        val items = PoArticleEntity.parse(result!!.data, page, Url.Question)

        //插入数据库
        baseDataBase.withTransaction {
            if (loadType == LoadType.REFRESH) {
                baseDataBase.articleDao().delete()
            }
            baseDataBase.articleDao().insert(items)
        }
        return MediatorResult.Success(endOfPaginationReached = result.over)
    }
}