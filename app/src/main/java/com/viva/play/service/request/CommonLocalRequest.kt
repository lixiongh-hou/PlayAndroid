package com.viva.play.service.request

import com.viva.play.db.BaseDataBase
import com.viva.play.db.entity.*
import com.viva.play.service.BaseResult
import com.viva.play.service.request.CommonRequest.Companion.noInternet
import com.viva.play.utils.CookieCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * @author 李雄厚
 *
 *
 */
class CommonLocalRequest @Inject constructor(
    private val baseDataBase: BaseDataBase
) {

    private suspend fun runInDispatcherIO(block: suspend () -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                block.invoke()
            } catch (e: Exception) {
                e.printStackTrace()
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

    suspend fun getArticleTop(callback: (BaseResult<List<PoArticleEntity>>) -> Unit) {
        runInDispatcherIO {
            val data = baseDataBase.homeArticleDao().findArticle(1)
            if (data.isNullOrEmpty()) {
                callback.invoke(BaseResult.Failure(noInternet))
            } else {
                callback.invoke(BaseResult.Success(data))
            }
        }
    }

    suspend fun getArticle(callback: (BaseResult<List<PoArticleEntity>>) -> Unit) {
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

}