package com.viva.play.utils

import android.content.Context
import com.viva.play.db.entity.PoHomeArticleEntity
import com.viva.play.ui.activity.ArticleActivity
import com.viva.play.ui.activity.WebActivity

/**
 * @author 李雄厚
 *
 *
 */
class UrlOpenUtils(private val url: String) {

    var title: String = ""

    var id: Int = -1
    var collectId: Int = -1
    var author: String = ""
    var collected: Boolean = false

    var userId: Int = -1

    var forceWeb: Boolean = false

    companion object {
        fun with(url: String) = UrlOpenUtils(url)

        fun with(article: PoHomeArticleEntity) = with(article.link).apply {
            article.let {
                id = it.id
                title = it.title
                collected = it.collect
                author = it.author
                userId = it.userId
            }
        }
    }

    fun open(context: Context?) {
        context ?: return
        if (!forceWeb && id > 0) {
            ArticleActivity.start(context, url, title, id, collected, author, userId)
        } else {
            WebActivity.start(context, url, title, id, collected, collectId)
        }
    }

}