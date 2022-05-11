package com.viva.play.service.request

import com.viva.play.db.BaseDataBase
import com.viva.play.db.entity.*
import com.viva.play.service.*
import com.viva.play.service.request.CommonRequest.Companion.noInternet
import com.viva.play.ui.vo.VoChapterEntity
import com.viva.play.utils.CookieCache
import kotlinx.coroutines.Dispatchers
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
            if (data.isNullOrEmpty()) {
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
            if (data.isNullOrEmpty()) {
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
        key: String,
        link: String,
        percent: Float,
        callback: (BaseResult<PoReadRecordEntity?>) -> Unit
    ) {
        runInDispatcherIO {
            baseDataBase.runInTransaction {
                val data = baseDataBase.bookDetailsDao().findReadRecord1(link, key)
                //如果不存在阅读记录缓存阅读记录
                if (data == null) {
                    baseDataBase.bookDetailsDao().insertReadRecord(
                        PoReadRecordEntity(
                            key,
                            link,
                            Date(),
                            (percent * PoBookDetailsEntity.MAX_PERCENT).toInt()
                        )
                    )
                } else {
                    baseDataBase.bookDetailsDao().updateReadRecordTime(
                        key,
                        link,
                        Date(),
                    )
                }
                callback.invoke(
                    BaseResult.Success(data)
                )
            }
        }
    }

    suspend fun updateReadRecordPercent(
        key: String,
        link: String,
        percent: Float,
        callback: (BaseResult<PoReadRecordEntity?>) -> Unit
    ) {
        runInDispatcherIO {
            baseDataBase.runInTransaction {
                val data =
                    baseDataBase.bookDetailsDao().findReadRecord1(link, key)
                        ?: return@runInTransaction
                val p = (percent.coerceIn(0f, 1f) * PoBookDetailsEntity.MAX_PERCENT).toInt()
                //如果传来的进度比缓存进度小不更新进度
                if (data.percent < p) {
                    baseDataBase.bookDetailsDao().updateReadRecord(key, link, p)
                }
                //时间实时更新
                baseDataBase.bookDetailsDao().updateReadRecordTime(key, link, Date())
                callback.invoke(BaseResult.Success(data))
            }
        }
    }

}