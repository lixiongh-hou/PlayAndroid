package com.viva.play.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.room.withTransaction
import com.viva.play.base.paging.BaseRemoteMediator
import com.viva.play.db.BaseDataBase
import com.viva.play.db.entity.PoCollectArticleEntity
import com.viva.play.service.CommonService

/**
 * @author 李雄厚
 *
 *
 */
@ExperimentalPagingApi
class CollectionArticleMediator(
    private val baseDataBase: BaseDataBase,
    private val commonService: CommonService
) : BaseRemoteMediator<PoCollectArticleEntity>() {

    override suspend fun loadData(pageKey: Int?, loadType: LoadType): MediatorResult {
        val page = pageKey ?: 0
        //请求网络分页数据
        val result = commonService.getCollectArticleList(page).data
        val items = PoCollectArticleEntity.parse(result!!, page)

        //插入数据库
        baseDataBase.withTransaction {
            if (loadType == LoadType.REFRESH) {
                baseDataBase.collectDao().deleteArticle()
            }
            baseDataBase.collectDao().insertArticle(items)
        }

        return MediatorResult.Success(endOfPaginationReached = result.over)
    }

}