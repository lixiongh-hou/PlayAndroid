package com.viva.play.service

/**
 * @author 李雄厚
 *
 * @features 公共常量管理类
 */
object Host {
    /**
     * 基础地址
     */
    const val HOST = "https://www.wanandroid.com/"
}

object Url {
    /**
     * 登录
     */
    const val Login = "user/login"

    /**
     * 注册
     */
    const val Register = "user/register"

    /**
     * 获取个人积分信息
     */
    const val CoinRecord = "lg/coin/userinfo/json"

    /**
     * 首页Banner
     */
    const val Banner = "banner/json"

    /**
     * 首页文章
     */
    const val Article = "article/list/{page}/json"

    /**
     * 搜索
     */
    const val Search = "article/query/{page}/json"

    /**
     * 首页文章置顶
     */
    const val ArticleTop = "article/top/json"

    /**
     * 收藏文章
     */
    const val CollectArticle = "lg/collect/{id}/json"

    /**
     * 取消收藏文章
     */
    const val UnCollectArticle = "lg/uncollect_originId/{id}/json"

    /**
     * 收藏网址
     */
    const val CollectLink = "lg/collect/addtool/json"

    /**
     * 删除收藏网站
     */
    const val UnCollectLink = "lg/collect/deletetool/json"

    /**
     * 收藏网址列表
     */
    const val CollectLinkList = "lg/collect/usertools/json"

    /**
     * 编辑网址
     */
    const val EditCollectLink = "lg/collect/updatetool/json"

    /**
     * 取消我的收藏页面文章
     */
    const val MyUnCollect = "lg/uncollect/{id}/json"

    /**
     * 我的收藏数据
     */
    const val MyCollect = "lg/collect/list/{page}/json"

    /**
     * 问答
     */
    const val Question = "wenda/list/{page}/json"

    /**
     * 体系
     */
    const val Tree = "tree/json"

    /**
     * 公众号名称
     */
    const val ProjectName = "wxarticle/chapters/json"

    /**
     * 公众号名称对应数据
     */
    const val ProjectList = "wxarticle/list/{id}/{page}/json"

    /**
     * 导航
     */
    const val Navigation = "project/tree/json"

    /**
     * 导航名称对应的数据
     */
    const val NavigationList = "project/list/{page}/json"

}

object FileKey {

    /**
     * 持久化存储的登录状态
     */
    const val COOKIE = "Cookie"

    /**
     * 用户Id
     */
    const val USER_ID = "userId"

    /**
     * 暗黑模式
     */
    const val KEY_DARK_THEME = "KEY_DARK_THEME"

    /**
     * 网络拦截规则
     */
    const val URL_INTERCEPT_TYPE = "URL_INTERCEPT_TYPE"

    /**
     * 网络拦截白名单
     */
    const val HOST_WHITE = "HOST_WHITE"

    /**
     * 网络拦截黑名单
     */
    const val HOST_BLACK = "HOST_BLACK"

}

object EventBus {

    /**
     * 通知站内文章收藏和取消收藏
     */
    const val COLLECTED = "COLLECTED"

    /**
     * 退出登录或者登录
     */
    const val LOGIN = "login"

}

object NetworkStatus {
    /**
     * 成功
     */
    const val SUCCESS = 0

    /**
     * 登录失败
     */
    const val LOGIN_ERROR = -1001

    /**
     * 没有网络
     */
    const val NO_INTERNET = 1011

    /**
     * 没有数据
     */
    const val EMPTY_DATA = 526
}