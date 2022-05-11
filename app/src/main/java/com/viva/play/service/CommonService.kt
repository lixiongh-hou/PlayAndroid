package com.viva.play.service

import com.viva.play.base.BaseResponse
import com.viva.play.service.Url.Article
import com.viva.play.service.Url.ArticleTop
import com.viva.play.service.Url.Banner
import com.viva.play.service.Url.Books
import com.viva.play.service.Url.ChapterArticle
import com.viva.play.service.Url.Coin
import com.viva.play.service.Url.CoinRecord
import com.viva.play.service.Url.CoinRecordList
import com.viva.play.service.Url.CollectArticle
import com.viva.play.service.Url.CollectLink
import com.viva.play.service.Url.CollectLinkList
import com.viva.play.service.Url.EditCollectLink
import com.viva.play.service.Url.Login
import com.viva.play.service.Url.MyCollect
import com.viva.play.service.Url.MyUnCollect
import com.viva.play.service.Url.Navi
import com.viva.play.service.Url.Question
import com.viva.play.service.Url.Tree
import com.viva.play.service.Url.UnCollectArticle
import com.viva.play.service.Url.UnCollectLink
import com.viva.play.service.Url.UserPage
import com.viva.play.service.entity.*
import retrofit2.http.*

/**
 * @author 李雄厚
 *
 *
 */
interface CommonService {

    /**
     * 获取Token
     */
    @GET("token")
    suspend fun getToken(): TokenEntity

    /**
     * 获取古诗词
     */
    @GET("sentence")
    suspend fun getPoetry(@Header("X-User-Token") token: String = "XyvoWt6yz23Dl0gjDP21zMyXxx2jqSsr"): PoetryEntity


    /**
     * 登录
     */
    @FormUrlEncoded
    @POST(Login)
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): BaseResponse<UserInfo>

    /**
     * 个人积分信息
     */
    @GET(CoinRecord)
    suspend fun getCoinRecordInfo(): BaseResponse<CoinRecordEntity>

    /**
     * 个人积分列表
     */
    @GET(CoinRecordList)
    suspend fun getCoinRecordList(@Path("page") page: Int): BaseResponse<CoinRecordListEntity>

    /**
     * 获取个人积分
     */
    @GET(Coin)
    suspend fun getCoin(): BaseResponse<Int>

    /**
     * 首页Banner图
     */
    @GET(Banner)
    suspend fun banner(): BaseResponse<List<BannerEntity>>

    /**
     * 首页置顶文章
     */
    @GET(ArticleTop)
    suspend fun articleTop(): BaseResponse<List<DataEntity>>

    /**
     * 首页文章
     *
     * @param page 页码从0开始，拼接在链接上
     */
    @GET(Article)
    suspend fun article(@Path("page") page: String): BaseResponse<ArticleEntity>

    /**
     * 收藏站内文章
     */
    @POST(CollectArticle)
    suspend fun collectArticle(@Path("id") id: Int): BaseResponse<Any?>

    /**
     * 取消收藏 文章列表
     */
    @POST(UnCollectArticle)
    suspend fun unCollectArticle(@Path("id") id: Int): BaseResponse<Any?>

    /**
     * 获取收藏文章列表
     */
    @GET(MyCollect)
    suspend fun getCollectArticleList(@Path("page") page: Int): BaseResponse<CollectArticleEntity>

    /**
     * 取消我的收藏
     * @param originId 代表的是你收藏之前的那篇文章本身的id； 但是收藏支持主动添加，这种情况下，没有originId则为-1
     */
    @FormUrlEncoded
    @POST(MyUnCollect)
    suspend fun unMyCollectArticle(
        @Path("id") id: Int,
        @Field("originId") originId: Int
    ): BaseResponse<Any?>

    /**
     * 收藏网址
     */
    @FormUrlEncoded
    @POST(CollectLink)
    suspend fun collectLink(
        @Field("name") name: String,
        @Field("link") link: String
    ): BaseResponse<CollectLinkEntity>

    /**
     * 删除收藏网址
     */
    @FormUrlEncoded
    @POST(UnCollectLink)
    suspend fun unCollectLink(
        @Field("id") id: Int
    ): BaseResponse<Any?>

    /**
     * 收藏网址列表
     */
    @GET(CollectLinkList)
    suspend fun getCollectLinkList(): BaseResponse<List<CollectLinkEntity>>

    /**
     * 编辑网址
     */
    @FormUrlEncoded
    @POST(EditCollectLink)
    suspend fun editCollectLink(
        @Field("id") id: Int,
        @Field("name") name: String,
        @Field("link") link: String,
    ): BaseResponse<CollectLinkEntity>

    /**
     * 问答
     */
    @GET(Question)
    suspend fun getQuestionList(@Path("page") page: Int): BaseResponse<ArticleEntity>

    /**
     * 体系
     */
    @GET(Tree)
    suspend fun getKnowledgeList(): BaseResponse<List<ChapterEntity>>

    /**
     * 导航
     */
    @GET(Navi)
    suspend fun getNaviList(): BaseResponse<List<NaviEntity>>

    /**
     * 分享人对应列表数据
     */
    @GET(UserPage)
    suspend fun getUserPage(
        @Path("userId") userId: Int,
        @Path("page") page: Int
    ): BaseResponse<UserPageEntity>

    /**
     * 知识体系下的文章
     */
    @GET(ChapterArticle)
    suspend fun getChapterArticleList(
        @Path("page") page: Int,
        @Query("cid") id: Int,
        @Query("order_type") orderType: Int = 0,
    ): BaseResponse<ArticleEntity>

    /**
     * 教程列表
     */
    @GET(Books)
    suspend fun getBooks(): BaseResponse<List<BookEntity>>


}