package com.viva.play.service.request

import androidx.paging.PagingSource
import androidx.room.withTransaction
import com.viva.play.db.BaseDataBase
import com.viva.play.db.entity.*
import com.viva.play.service.ApiError
import com.viva.play.service.BaseResult
import com.viva.play.service.NetworkStatus.EMPTY_DATA
import com.viva.play.service.NetworkStatus.NO_INTERNET
import com.viva.play.service.doFailure
import com.viva.play.service.doSuccess
import com.viva.play.service.entity.*
import com.viva.play.utils.CookieCache
import com.viva.play.utils.NetworkUtils.isNetworkAvailable
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * @author 李雄厚
 *
 *
 */
class CommonRequest @Inject constructor() {

    @Inject
    lateinit var remoteRequest: CommonRemoteRequest

    @Inject
    lateinit var localRequest: CommonLocalRequest

    @Inject
    lateinit var baseDataBase: BaseDataBase

    companion object {
        val emptyData = ApiError(code = EMPTY_DATA, "数据为NULL")

        val noInternet = ApiError(code = NO_INTERNET, "网络连接失败")
    }

    /**
     * 首页数据三合一
     */
    fun getHomeData(
        scope: CoroutineScope,
        local: Boolean = false,
        callback: (BaseResult<HomeEntity>) -> Unit
    ) {
        scope.launch {
            withContext(Dispatchers.IO) {
                val banner = async {
                    getBanner(local)
                }
                val articleTop = async {
                    getArticleTop(local)
                }
                val article = async {
                    getArticleAsync(local = local)
                }
                val bannerData = banner.await()
                val articleTopData = articleTop.await()
                val articleData = article.await()
                if (bannerData == null || articleTopData == null || articleData == null) {
                    callback.invoke(BaseResult.Failure(noInternet))
                } else {
                    val data = mutableListOf<PoArticleEntity>()
                    data.addAll(articleTopData)
                    data.addAll(articleData)
                    callback.invoke(BaseResult.Success(HomeEntity(bannerData, data)))
                }
            }
        }
    }

    /**
     * 获取首页Banner
     */
    private suspend fun getBanner(local: Boolean): List<PoBannerEntity>? {
        var list: MutableList<PoBannerEntity>? = null
        val total = baseDataBase.homeArticleDao().findBannerTotal
        //判断取本地数据还是网络数据
        if (isNetworkAvailable() && !local) {
            remoteRequest.getBanner {
                it.doSuccess { success ->
                    list = mutableListOf()
                    list?.addAll(PoBannerEntity.parse(success))

                    baseDataBase.runInTransaction {
                        baseDataBase.homeArticleDao().deleteBanner()
                        baseDataBase.homeArticleDao().insertBanner(list!!)
                    }

                }
                it.doFailure {
                    list = null
                }
            }
        } else {
            if (total == 0) {
                remoteRequest.getBanner {
                    it.doSuccess { success ->
                        list = mutableListOf()
                        list?.addAll(PoBannerEntity.parse(success))

                        baseDataBase.runInTransaction {
                            baseDataBase.homeArticleDao().deleteBanner()
                            baseDataBase.homeArticleDao().insertBanner(list!!)
                        }

                    }
                    it.doFailure {
                        list = null
                    }
                }
            } else {
                localRequest.getBanner {
                    it.doSuccess { success ->
                        list = mutableListOf()
                        list?.addAll(success)
                    }
                    it.doFailure {
                        list = null
                    }
                }
            }
        }

        return list
    }

    /**
     * 获取置顶文章
     */
    private suspend fun getArticleTop(local: Boolean): List<PoArticleEntity>? {
        var list: MutableList<PoArticleEntity>? = null
        val total = baseDataBase.homeArticleDao().findTypeTotal(1)
        //判断取本地数据还是网络数据
        if (isNetworkAvailable() && !local) {
            remoteRequest.getArticleTop {
                it.doSuccess { success ->
                    list = mutableListOf()
                    list?.addAll(PoArticleEntity.parse(success))

                    baseDataBase.homeArticleDao().deleteArticle(1)
                    baseDataBase.homeArticleDao().insert(list!!)
                }
                it.doFailure {
                    list = null
                }
            }
        } else {
            if (total == 0) {
                remoteRequest.getArticleTop {
                    it.doSuccess { success ->
                        list = mutableListOf()
                        list?.addAll(PoArticleEntity.parse(success))

                        baseDataBase.homeArticleDao().deleteArticle(1)
                        baseDataBase.homeArticleDao().insert(list!!)
                    }
                    it.doFailure {
                        list = null
                    }
                }
            } else {
                localRequest.getArticleTop {
                    it.doSuccess { success ->
                        list = mutableListOf()
                        list?.addAll(success)
                    }
                    it.doFailure {
                        list = null
                    }
                }
            }
        }

        return list
    }

    private suspend fun getArticleAsync(page: Int = 0, local: Boolean): List<PoArticleEntity>? {
        var list: MutableList<PoArticleEntity>? = null
        val total = baseDataBase.homeArticleDao().findTypeTotal(0)
        //判断取本地数据还是网络数据
        if (isNetworkAvailable() && !local) {
            remoteRequest.getArticle(page) {
                it.doSuccess { success ->
                    list = mutableListOf()
                    list?.addAll(PoArticleEntity.parse(success.data))

                    baseDataBase.homeArticleDao().deleteArticle(0)
                    baseDataBase.homeArticleDao().insert(list!!)
                }
                it.doFailure {
                    list = null
                }
            }
        } else {
            if (total == 0) {
                remoteRequest.getArticle(page) {
                    it.doSuccess { success ->
                        list = mutableListOf()
                        list?.addAll(PoArticleEntity.parse(success.data))

                        baseDataBase.homeArticleDao().deleteArticle(0)
                        baseDataBase.homeArticleDao().insert(list!!)
                    }
                    it.doFailure {
                        list = null
                    }
                }
            } else {
                localRequest.getArticle {
                    it.doSuccess { success ->
                        list = mutableListOf()
                        list?.addAll(success)
                    }
                    it.doFailure {
                        list = null
                    }
                }
            }

        }

        return list
    }

    /**
     * 获取首页文章
     */
    fun getArticle(
        scope: CoroutineScope,
        page: Int = 0,
        callback: (BaseResult<List<PoArticleEntity>>) -> Unit
    ) {
        scope.launch {
            if (isNetworkAvailable()) {
                remoteRequest.getArticle(page) {
                    it.doSuccess { success ->
                        callback.invoke(BaseResult.Success(PoArticleEntity.parse(success.data)))
                    }
                    it.doFailure { error ->
                        callback.invoke(BaseResult.Failure(error))
                    }
                }
            } else {
                callback.invoke(BaseResult.Failure(noInternet))
            }

        }
    }

    /**
     * 登录
     */
    fun login(
        scope: CoroutineScope,
        username: String,
        password: String,
        callback: (BaseResult<PoUserInfo>) -> Unit
    ) {
        scope.launch {
            if (isNetworkAvailable()) {
                remoteRequest.login(username, password) {
                    it.doSuccess { success ->
                        val userInfo = baseDataBase.userInfoDao().findUserInfo(success.id)
                        if (userInfo == null) {
                            baseDataBase.userInfoDao().insert(PoUserInfo.parse(success))
                            callback.invoke(BaseResult.Success(PoUserInfo.parse(success)))
                        } else {
                            callback.invoke(BaseResult.Success(userInfo))
                        }
                        CookieCache.userId = success.id
                    }
                    it.doFailure { apiError ->
                        callback.invoke(BaseResult.Failure(apiError))
                    }
                }
            } else {
                callback.invoke(BaseResult.Failure(noInternet))
            }
        }
    }

    /**
     * 收藏站内文章
     */
    fun collectArticle(scope: CoroutineScope, id: Int, callback: (BaseResult<Any?>) -> Unit) {
        scope.launch {
            if (isNetworkAvailable()) {
                remoteRequest.collectArticle(id) {
                    it.doSuccess { success ->
                        baseDataBase.runInTransaction {
                            baseDataBase.homeArticleDao().collectArticle(id, true)
                            baseDataBase.questionDao().collectQuestion(id, true)
                        }
                        callback.invoke(BaseResult.Success(success))
                    }
                    it.doFailure { apiError ->
                        callback.invoke(BaseResult.Failure(apiError))
                    }
                }
            } else {
                callback.invoke(BaseResult.Failure(noInternet))
            }
        }
    }

    /**
     * 取消收藏站内文章
     */
    fun unCollectArticle(scope: CoroutineScope, id: Int, callback: (BaseResult<Any?>) -> Unit) {
        scope.launch {
            if (isNetworkAvailable()) {
                remoteRequest.unCollectArticle(id) {
                    it.doSuccess { success ->
                        baseDataBase.runInTransaction {
                            baseDataBase.homeArticleDao().collectArticle(id, false)
                            baseDataBase.questionDao().collectQuestion(id, false)
                        }
                        callback.invoke(BaseResult.Success(success))
                    }
                    it.doFailure { apiError ->
                        callback.invoke(BaseResult.Failure(apiError))
                    }
                }
            } else {
                callback.invoke(BaseResult.Failure(noInternet))
            }
        }
    }

    /**
     * 我的收藏文章列表
     */
//    suspend fun getCollectArticleList(
//        page: Int = 0,
//    ): BaseSourceData<PoCollectArticleEntity> {
//        //page==0代表刷新或者是用户第一次进来时候加载的第一页数据
//        return if (isNetworkAvailable()) {
//            val remoteData = remoteRequest.getCollectArticleList(page)
//            if (page == 0) {
//                baseDataBase.collectDao().deleteArticle()
//            }
//            baseDataBase.collectDao()
//                .insertArticle(PoCollectArticleEntity.parse(remoteData))
//            BaseSourceData(remoteData.over, PoCollectArticleEntity.parse(remoteData))
//        } else {
//            if (page == 0) {
//                val total = baseDataBase.collectDao().findArticleTotal()
//                BaseSourceData(total < 20, localRequest.getCollectArticleList())
//            } else {
//                val remoteData = remoteRequest.getCollectArticleList(page)
//                BaseSourceData(remoteData.over, PoCollectArticleEntity.parse(remoteData))
//            }
//
//        }
//    }

    /**
     * 取消我的收藏
     */
    fun unMyCollectArticle(
        scope: CoroutineScope,
        id: Int,
        originId: Int,
        callback: (BaseResult<Any?>) -> Unit
    ) {
        scope.launch {
            if (isNetworkAvailable()) {
                remoteRequest.unMyCollectArticle(id, originId) {
                    it.doSuccess { success ->
                        baseDataBase.collectDao().deleteArticle(id, originId)
                        callback.invoke(BaseResult.Success(success))

                    }
                    it.doFailure { apiError ->
                        callback.invoke(BaseResult.Failure(apiError))
                    }
                }
            } else {
                callback.invoke(BaseResult.Failure(noInternet))
            }
        }

    }

    /**
     * 收藏网址
     */
    fun collectLink(
        scope: CoroutineScope,
        name: String,
        link: String,
        callback: (BaseResult<CollectLinkEntity?>) -> Unit
    ) {
        if (isNetworkAvailable()) {
            scope.launch {
                remoteRequest.collectLink(name, link) {
                    it.doSuccess { success ->
                        if (success != null) {
                            baseDataBase.collectDao().insertLink(PoCollectLinkEntity.parse(success))
                            callback.invoke(BaseResult.Success(success))
                        } else {
                            callback.invoke(BaseResult.Failure(emptyData))
                        }

                    }
                    it.doFailure { apiError ->
                        callback.invoke(BaseResult.Failure(apiError))
                    }
                }
            }
        } else {
            callback.invoke(BaseResult.Failure(noInternet))

        }

    }


    /**
     * 取消收藏网址
     */
    fun unCollectLink(
        scope: CoroutineScope,
        id: Int,
        callback: (BaseResult<Any?>) -> Unit
    ) {
        if (isNetworkAvailable()) {
            scope.launch {
                remoteRequest.unCollectLink(id) {
                    it.doSuccess { success ->
                        baseDataBase.collectDao().deleteLink(id)
                        callback.invoke(BaseResult.Success(success))
                    }
                    it.doFailure { apiError ->
                        callback.invoke(BaseResult.Failure(apiError))
                    }
                }
            }
        } else {
            callback.invoke(BaseResult.Failure(noInternet))

        }

    }

    /**
     * 编辑网址
     */
    fun editCollectLink(
        scope: CoroutineScope,
        data: PoCollectLinkEntity,
        callback: (BaseResult<PoCollectLinkEntity?>) -> Unit
    ) {
        if (isNetworkAvailable()) {
            scope.launch {
                remoteRequest.editCollectLink(data.collectId, data.title, data.url) {
                    it.doSuccess { success ->
                        baseDataBase.collectDao().updateLink(data)
                        callback.invoke(BaseResult.Success(success?.let { it1 ->
                            PoCollectLinkEntity.parse(
                                it1
                            )
                        }))
                    }
                    it.doFailure { apiError ->
                        callback.invoke(BaseResult.Failure(apiError))
                    }
                }
            }
        } else {
            callback.invoke(BaseResult.Failure(noInternet))
        }
    }

    /**
     * 获取网址列表
     */
    fun getCollectLinkList(
        scope: CoroutineScope,
        local: Boolean = false,
        callback: (BaseResult<List<PoCollectLinkEntity>>) -> Unit
    ) {
        scope.launch {
            if (local) {
                localRequest.getCollectLinkList(callback)
            } else {
                if (isNetworkAvailable()) {
                    scope.launch {
                        remoteRequest.getCollectLinkList {
                            it.doSuccess { success ->
                                callback.invoke(BaseResult.Success(PoCollectLinkEntity.parse(success)))
                            }
                            it.doFailure { apiError ->
                                callback.invoke(BaseResult.Failure(apiError))
                            }
                        }
                    }
                } else {
                    localRequest.getCollectLinkList(callback)
                }
            }
        }
    }

    /**
     * 获取网址列表 paging结合Room使用的
     * 这边只从本地获取数据，网络结合本地一起使用还在学习当中
     */
    fun getCollectLinkList(): PagingSource<Int, PoCollectLinkEntity> {
        return baseDataBase.collectDao().findLink()
    }


    /**
     * 获取个人积分信息
     */
    fun getCoinRecordInfo(
        scope: CoroutineScope,
        callback: (BaseResult<CoinRecordEntity>) -> Unit
    ) {
        scope.launch {
            if (isNetworkAvailable()) {
                remoteRequest.getCoinRecordInfo {
                    it.doSuccess { success ->
                        baseDataBase.userInfoDao().updateRank(success.userId, success.rank)
                        callback.invoke(BaseResult.Success(success))
                    }
                    it.doFailure { apiError ->
                        callback.invoke(BaseResult.Failure(apiError))
                    }
                }
            } else {
                callback.invoke(BaseResult.Failure(noInternet))
            }
        }

    }

    /**
     * 获取用户信息
     */
    fun getUserInfo(
        scope: CoroutineScope,
        callback: (BaseResult<PoUserInfo>) -> Unit
    ) {
        scope.launch {
            localRequest.getUserInfo(callback)
        }
    }

}