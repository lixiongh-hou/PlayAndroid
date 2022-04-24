package com.viva.play.service.request

import com.viva.play.di.MadeInCommon
import com.viva.play.service.*
import com.viva.play.service.DataConvert.convert
import com.viva.play.service.DataConvert.convertNetworkError
import com.viva.play.service.entity.*
import com.viva.play.service.request.CommonRequest.Companion.emptyData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * @author 李雄厚
 *
 *
 */
class CommonRemoteRequest @Inject constructor(
    @MadeInCommon
    private val service: CommonService
) {

    private suspend fun runInDispatcherIO(block: suspend () -> Unit, error: (ApiError) -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                block.invoke()
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is ServerException) {
                    error.invoke(ApiError(e.code, e.message ?: ""))
                } else {
                    e.convertNetworkError {
                        error.invoke(it)
                    }
                }
            }
        }
    }


    suspend fun getBanner(callback: (BaseResult<List<BannerEntity>>) -> Unit) {
        runInDispatcherIO(
            block = {
                service.banner().convert {
                    it?.let { data ->
                        callback.invoke(BaseResult.Success(data))
                    } ?: run {
                        callback.invoke(BaseResult.Failure(emptyData))
                    }
                }
            },
            error = {
                callback.invoke(BaseResult.Failure(it))
            }
        )
    }

    suspend fun getArticleTop(callback: (BaseResult<List<DataEntity>>) -> Unit) {
        runInDispatcherIO(
            block = {
                service.articleTop().convert {
                    it?.let { data ->
                        callback.invoke(BaseResult.Success(data))
                    } ?: run {
                        callback.invoke(BaseResult.Failure(emptyData))
                    }
                }
            },
            error = {
                callback.invoke(BaseResult.Failure(it))
            }
        )
    }

    suspend fun getArticle(page: Int, callback: (BaseResult<ArticleEntity>) -> Unit) {
        runInDispatcherIO(
            block = {
                service.article(page.toString()).convert {
                    it?.let { data ->
                        callback.invoke(BaseResult.Success(data))
                    } ?: run {
                        callback.invoke(BaseResult.Failure(emptyData))
                    }
                }
            },
            error = {
                callback.invoke(BaseResult.Failure(it))
            }
        )
    }

    suspend fun login(
        username: String,
        password: String,
        callback: (BaseResult<UserInfo>) -> Unit
    ) {
        runInDispatcherIO(
            block = {
                service.login(username, password).convert {
                    it?.let { data ->
                        callback.invoke(BaseResult.Success(data))
                    } ?: run {
                        callback.invoke(BaseResult.Failure(emptyData))
                    }
                }
            },
            error = {
                callback.invoke(BaseResult.Failure(it))
            }
        )
    }

    suspend fun getCoinRecordInfo(callback: (BaseResult<CoinRecordEntity>) -> Unit) {
        runInDispatcherIO(
            block = {
                service.getCoinRecordInfo().convert {
                    it?.let { data ->
                        callback.invoke(BaseResult.Success(data))
                    } ?: run {
                        callback.invoke(BaseResult.Failure(emptyData))
                    }
                }
            },
            error = {
                callback.invoke(BaseResult.Failure(it))
            }
        )
    }

    suspend fun collectArticle(id: Int, callback: (BaseResult<Any?>) -> Unit) {
        runInDispatcherIO(
            block = {
                service.collectArticle(id).convert {
                    callback.invoke(BaseResult.Success("收藏成功"))
                }
            },
            error = {
                callback.invoke(BaseResult.Failure(it))
            }
        )
    }

//    suspend fun getCollectArticleList(
//        page: Int = 0
//    ): CollectArticleEntity {
//        return service.getCollectArticleList(page).data!!
//    }


    suspend fun unCollectArticle(id: Int, callback: (BaseResult<Any?>) -> Unit) {
        runInDispatcherIO(
            block = {
                service.unCollectArticle(id).convert {
                    callback.invoke(BaseResult.Success("取消收藏"))
                }
            },
            error = {
                callback.invoke(BaseResult.Failure(it))
            }
        )
    }

    suspend fun unMyCollectArticle(
        id: Int,
        originId: Int,
        callback: (BaseResult<Any?>) -> Unit
    ) {
        runInDispatcherIO(
            block = {
                service.unMyCollectArticle(id, originId).convert {
                    callback.invoke(BaseResult.Success("取消收藏"))
                }
            },
            error = {
                callback.invoke(BaseResult.Failure(it))
            }
        )
    }

    suspend fun collectLink(
        name: String,
        link: String,
        callback: (BaseResult<CollectLinkEntity?>) -> Unit
    ) {
        runInDispatcherIO(
            block = {
                service.collectLink(name, link).convert {
                    callback.invoke(BaseResult.Success(it))
                }
            },
            error = {
                callback.invoke(BaseResult.Failure(it))
            }
        )
    }

    suspend fun unCollectLink(id: Int, callback: (BaseResult<Any?>) -> Unit) {
        runInDispatcherIO(
            block = {
                service.unCollectLink(id).convert {
                    callback.invoke(BaseResult.Success("取消收藏"))
                }
            },
            error = {
                callback.invoke(BaseResult.Failure(it))
            }
        )
    }

    suspend fun getCollectLinkList(callback: (BaseResult<List<CollectLinkEntity>>) -> Unit) {
        runInDispatcherIO(
            block = {
                service.getCollectLinkList().convert {
                    it?.let { data ->
                        callback.invoke(BaseResult.Success(data))
                    } ?: run {
                        callback.invoke(BaseResult.Failure(emptyData))
                    }
                }
            },
            error = {
                callback.invoke(BaseResult.Failure(it))
            }
        )
    }

    suspend fun editCollectLink(
        id: Int,
        name: String,
        link: String,
        callback: (BaseResult<CollectLinkEntity?>) -> Unit
    ) {
        runInDispatcherIO(
            block = {
                service.editCollectLink(id, name, link).convert {
                    callback.invoke(BaseResult.Success(it))
                }
            },
            error = {
                callback.invoke(BaseResult.Failure(it))
            }
        )
    }

    /**
     * 体系列表
     */
    suspend fun getKnowledgeList(callback: (BaseResult<List<ChapterEntity>>) -> Unit) {
        runInDispatcherIO(
            block = {
                service.getKnowledgeList().convert {
                    it?.let { data ->
                        callback.invoke(BaseResult.Success(data))
                    } ?: run {
                        callback.invoke(BaseResult.Failure(emptyData))
                    }
                }
            },
            error = {
                callback.invoke(BaseResult.Failure(it))
            }
        )
    }
}