package com.viva.play.paging

import com.viva.play.base.paging.BasePagingSource
import com.viva.play.base.paging.BaseSourceData
import com.viva.play.service.entity.CoinRecord
import com.viva.play.service.request.CommonRequest

/**
 * @author 李雄厚
 *
 *
 */
class CoinPagingSource(
    private val commonRequest: CommonRequest
) : BasePagingSource<CoinRecord>() {
    override suspend fun loadData(page: Int, offset: Int): BaseSourceData<CoinRecord> {
        val data = commonRequest.getCoinRecordList(page)
        return BaseSourceData(data.over, data.data)
    }
}