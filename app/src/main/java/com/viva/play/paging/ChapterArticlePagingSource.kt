package com.viva.play.paging

import com.viva.play.base.paging.BasePagingSource
import com.viva.play.base.paging.BaseSourceData
import com.viva.play.db.entity.PoArticleEntity
import com.viva.play.service.request.CommonRequest

/**
 * @author 李雄厚
 *
 *
 */
class ChapterArticlePagingSource(
    private val id: Int,
    private val commonRequest: CommonRequest
) : BasePagingSource<PoArticleEntity>() {
    override suspend fun loadData(page: Int, offset: Int): BaseSourceData<PoArticleEntity> {
        val data = commonRequest.getChapterArticleList(page, id)
        return BaseSourceData(data.over, PoArticleEntity.parse(data.data, page, ""))
    }
}