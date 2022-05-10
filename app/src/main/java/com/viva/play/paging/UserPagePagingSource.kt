package com.viva.play.paging

import com.viva.play.base.BasePagingSource
import com.viva.play.base.BaseSourceData
import com.viva.play.db.entity.PoArticleEntity
import com.viva.play.service.CommonService

/**
 * @author 李雄厚
 *
 *
 */
class UserPagePagingSource(
    private val userId: Int,
    private val commonService: CommonService
) : BasePagingSource<PoArticleEntity>() {
    override suspend fun loadData(page: Int, offset: Int): BaseSourceData<PoArticleEntity> {
        val data = commonService.getUserPage(userId, page)
        return BaseSourceData(
            data.data!!.shareArticles.over,
            PoArticleEntity.parse(data.data.shareArticles.data, page, "")
        )
    }
}