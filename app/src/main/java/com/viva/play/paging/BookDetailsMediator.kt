package com.viva.play.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.room.withTransaction
import com.viva.play.base.paging.BaseRemoteMediator
import com.viva.play.db.BaseDataBase
import com.viva.play.db.entity.PoBookDetailsEntity
import com.viva.play.db.entity.PoReadRecordEntity
import com.viva.play.service.CommonService
import com.viva.play.service.Url

/**
 * @author 李雄厚
 *
 *
 */
@ExperimentalPagingApi
class BookDetailsMediator(
    private val baseDataBase: BaseDataBase,
    private val commonService: CommonService,
    private val cid: Int,
) : BaseRemoteMediator<PoBookDetailsEntity>() {
    override suspend fun loadData(pageKey: Int?, loadType: LoadType): MediatorResult {

        //请求网络分页数据
        val page = pageKey ?: 0
        val result = commonService.getChapterArticleList(page, cid, 1).data
        val key = Url.ChapterArticle.replace("{page}", cid.toString())

        val readRecord = result!!.data.map {
            val data = baseDataBase.bookDetailsDao().findReadRecord(it.link)
            if (data == null) {
                PoReadRecordEntity(it.id, it.author, it.userId, it.link, it.title, null, 0)
            } else {
                PoReadRecordEntity(
                    it.id,
                    it.author,
                    it.userId,
                    it.link,
                    it.title,
                    data.lastTime,
                    data.percent
                )
            }
        }

        val items = PoBookDetailsEntity.parse(result.data, readRecord, page, key)
        //插入数据库
        baseDataBase.withTransaction {
            if (loadType == LoadType.REFRESH) {
                baseDataBase.bookDetailsDao().delete(key)
            }
            baseDataBase.bookDetailsDao().insert(items)
        }
        return MediatorResult.Success(endOfPaginationReached = result.over)
    }
}