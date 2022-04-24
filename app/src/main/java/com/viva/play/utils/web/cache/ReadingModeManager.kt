package com.viva.play.utils.web.cache

import com.viva.play.utils.web.interceptor.WebArticleUrlRegexBean

object ReadingModeManager {

    //https://gitee.com/goweii/WanAndroidServer/raw/master/web/article.json
    private val urlRegexList by lazy {
        val list = mutableListOf<WebArticleUrlRegexBean>()
        list.add(WebArticleUrlRegexBean("csdn", "csdn.net", "https://.*blog.csdn.net/.*article/details/.*"))
        list.add(WebArticleUrlRegexBean("jianshu", "jianshu.com", "https://www.jianshu.com/p/.*"))
        list.add(WebArticleUrlRegexBean("juejin", "juejin.cn", "https://juejin.cn/post/.*"))
        list.add(WebArticleUrlRegexBean("weixin", "mp.weixin.qq.com", "https://mp.weixin.qq.com/s/.*"))
        list.add(WebArticleUrlRegexBean("zhihu", "zhuanlan.zhihu.com", "https://zhuanlan.zhihu.com/p/.*"))
        list
    }

    fun getUrlRegexBeanForHost(host: String?): WebArticleUrlRegexBean? {
        if (urlRegexList.isEmpty()) return null
        host ?: return null
        return urlRegexList.firstOrNull { host.contains(it.host) }
    }
}