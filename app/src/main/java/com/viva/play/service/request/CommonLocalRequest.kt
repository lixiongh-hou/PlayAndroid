package com.viva.play.service.request

import com.viva.play.db.BaseDataBase
import com.viva.play.db.entity.*
import com.viva.play.service.*
import com.viva.play.service.request.CommonRequest.Companion.noInternet
import com.viva.play.ui.vo.VoChapterEntity
import com.viva.play.utils.CookieCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

/**
 * @author 李雄厚
 *
 *
 */
class CommonLocalRequest @Inject constructor(
    private val baseDataBase: BaseDataBase
) {

    private suspend fun runInDispatcherIO(
        block: suspend (BaseResult<String>) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            try {
                block.invoke(BaseResult.Success("成功"))
            } catch (e: Exception) {
                e.printStackTrace()
                block.invoke(BaseResult.Failure(ApiError(code = -1, message = "数据库操作错误")))
            }
        }
    }

    suspend fun getBanner(callback: (BaseResult<List<PoBannerEntity>>) -> Unit) {
        runInDispatcherIO {
            val data = baseDataBase.homeArticleDao().findBanner
            if (data.isEmpty()) {
                callback.invoke(BaseResult.Failure(noInternet))
            } else {
                callback.invoke(BaseResult.Success(data))
            }
        }
    }

    suspend fun getArticleTop(callback: (BaseResult<List<PoHomeArticleEntity>>) -> Unit) {
        runInDispatcherIO {
            val data = baseDataBase.homeArticleDao().findArticle(1)
            if (data.isNullOrEmpty()) {
                callback.invoke(BaseResult.Failure(noInternet))
            } else {
                callback.invoke(BaseResult.Success(data))
            }
        }
    }

    suspend fun getArticle(callback: (BaseResult<List<PoHomeArticleEntity>>) -> Unit) {
        runInDispatcherIO {
            val data = baseDataBase.homeArticleDao().findArticle(0)
            if (data.isNullOrEmpty()) {
                callback.invoke(BaseResult.Failure(noInternet))
            } else {
                callback.invoke(BaseResult.Success(data))
            }
        }
    }

//    suspend fun getCollectArticleList(): List<PoCollectArticleEntity> {
//        val data = baseDataBase.collectDao().findArticle()
//        return if (data.isNullOrEmpty()) {
//            emptyList()
//        } else {
//            data
//        }
//    }

    suspend fun getCollectLinkList(callback: (BaseResult<List<PoCollectLinkEntity>>) -> Unit) {
        runInDispatcherIO {
            val data = baseDataBase.collectDao().findLink
            if (data.isEmpty()) {
                callback.invoke(BaseResult.Failure(noInternet))
            } else {
                callback.invoke(BaseResult.Success(data))
            }
        }
    }

    suspend fun getUserInfo(callback: (BaseResult<PoUserInfo>) -> Unit) {
        runInDispatcherIO {
            val data = baseDataBase.userInfoDao().findUserInfo(CookieCache.userId)
            if (data == null) {
                callback.invoke(BaseResult.Failure(noInternet))
            } else {
                callback.invoke(BaseResult.Success(data))
            }
        }
    }

    suspend fun getKnowledgeList(callback: (BaseResult<List<VoChapterEntity>>) -> Unit) {
        runInDispatcherIO {
            val data = baseDataBase.chapterDao().findKnowledge()
            callback.invoke(BaseResult.Success(data))
        }
    }

    suspend fun getNaviList(callback: (BaseResult<List<VoChapterEntity>>) -> Unit) {
        runInDispatcherIO {
            val data = baseDataBase.chapterDao().findNavi()
            callback.invoke(BaseResult.Success(data))
        }
    }

    suspend fun addReadLater(data: PoReadLaterEntity, callback: (BaseResult<String>) -> Unit) {
        runInDispatcherIO {
            baseDataBase.readLaterDao().insert(data)
            callback.invoke(it)
        }
    }

    suspend fun removeReadLater(link: String, callback: (BaseResult<String>) -> Unit) {
        runInDispatcherIO {
            val int = baseDataBase.readLaterDao().delete(link)
            it.doSuccess { success ->
                if (int > 0) {
                    callback.invoke(BaseResult.Success(success))
                } else {
                    callback.invoke(BaseResult.Failure(ApiError(code = -1, message = "数据库操作错误")))
                }
            }
            it.doFailure { apiError ->
                callback.invoke(BaseResult.Failure(apiError))
            }

        }
    }

    suspend fun isReadLater(link: String, callback: (BaseResult<Boolean>) -> Unit) {
        runInDispatcherIO {
            it.doSuccess {
                val data = baseDataBase.readLaterDao().isReadLater(link)
                if (data == null) {
                    callback.invoke(BaseResult.Success(false))
                } else {
                    callback.invoke(BaseResult.Success(true))
                }
            }
            it.doFailure { apiError ->
                callback.invoke(BaseResult.Failure(apiError))
            }

        }
    }

    suspend fun removeReadLaterAll(callback: (BaseResult<String>) -> Unit) {
        runInDispatcherIO {
            baseDataBase.readLaterDao().delete()
            callback.invoke(it)
        }
    }

    suspend fun addReadRecord(
        id: Int,
        author: String,
        userId: Int,
        link: String,
        title: String,
        percent: Float,
        callback: (BaseResult<PoReadRecordEntity?>) -> Unit
    ) {
        runInDispatcherIO {
            baseDataBase.runInTransaction {
                val p = (percent.coerceIn(0f, 1f) * PoBookDetailsEntity.MAX_PERCENT).toInt()
                val data = baseDataBase.bookDetailsDao().findReadRecord1(id, link)
                baseDataBase.bookDetailsDao().insertReadRecord(
                    PoReadRecordEntity(
                        id,
                        author,
                        userId,
                        link,
                        title,
                        Date(),
                        p
                    )
                )
                callback.invoke(
                    BaseResult.Success(data)
                )
            }
        }
    }

    suspend fun updateReadRecordPercent(
        id: Int,
        link: String,
        percent: Float,
        callback: (BaseResult<PoReadRecordEntity?>) -> Unit
    ) {
        runInDispatcherIO {
            baseDataBase.runInTransaction {
                val data =
                    baseDataBase.bookDetailsDao().findReadRecord1(id, link)
                        ?: return@runInTransaction
                val p = (percent.coerceIn(0f, 1f) * PoBookDetailsEntity.MAX_PERCENT).toInt()
                //如果传来的进度比缓存进度小不更新进度只更新时间
                if (data.percent < p) {
                    baseDataBase.bookDetailsDao().updateReadRecord(id, link, p, Date())
                } else {
                    //时间实时更新
                    baseDataBase.bookDetailsDao().updateReadRecordTime(id, link, Date())
                }

                callback.invoke(BaseResult.Success(data))
            }
        }
    }

    suspend fun delReadRecord(data: PoReadRecordEntity, callback: (BaseResult<String>) -> Unit) {
        runInDispatcherIO {
            baseDataBase.bookDetailsDao().delReadRecord(data)
            callback.invoke(it)
        }
    }

    suspend fun delAllReadRecord(callback: (BaseResult<String>) -> Unit) {
        runInDispatcherIO {
            baseDataBase.bookDetailsDao().delAllReadRecord()
            callback.invoke(it)
        }
    }

    suspend fun saveSearchHistory(
        data: PoSearchHistoryEntity,
        callback: (BaseResult<String>) -> Unit
    ) {
        runInDispatcherIO {
            baseDataBase.searchHistoryDao().insertHistory(data)
            callback.invoke(it)
        }
    }

    suspend fun getSearchHistory(
        callback: (BaseResult<List<PoSearchHistoryEntity>>) -> Unit
    ) {
        runInDispatcherIO {
            callback.invoke(BaseResult.Success(baseDataBase.searchHistoryDao().findSearchHistory))
        }
    }

    suspend fun delHistory(
        data: PoSearchHistoryEntity,
        callback: (BaseResult<String>) -> Unit
    ) {
        runInDispatcherIO {
            baseDataBase.searchHistoryDao().delete(data)
            callback.invoke(it)
        }
    }
    suspend fun delAllHistory(callback: (BaseResult<String>) -> Unit){
        runInDispatcherIO{
            baseDataBase.searchHistoryDao().deleteAll()
        }
    }

    fun getHotKeyList(): Flow<List<PoHotKeyEntity>> =
        baseDataBase.searchHistoryDao().findHotKey()
}