package com.viva.play.service.entity

/**
 * @author 李雄厚
 *
 * 统一文章列表数据实体类
 */
data class DataEntity(

    /**
    "apkLink": "",
    "audit": 1,
    "author": "郭霖",
    "canEdit": false,
    "chapterId": 409,
    "chapterName": "郭霖",
    "collect": false,
    "courseId": 13,
    "desc": "",
    "descMd": "",
    "envelopePic": "",
    "fresh": false,
    "host": "",
    "id": 22092,
    "link": "https://mp.weixin.qq.com/s/a145TNBpAlqnrE0hlRjm0g",
    "niceDate": "2022-03-29 00:00",
    "niceShareDate": "2022-03-29 22:11",
    "origin": "",
    "prefix": "",
    "projectLink": "",
    "publishTime": 1648483200000,
    "realSuperChapterId": 407,
    "selfVisible": 0,
    "shareDate": 1648563089000,
    "shareUser": "",
    "superChapterId": 408,
    "superChapterName": "公众号",
    "tags": [
    {
    "name": "公众号",
    "url": "/wxarticle/list/409/1"
    }
    ],
    "title": "实战，在Compose中使用Navigation导航",
    "type": 0,
    "userId": -1,
    "visible": 1,
    "zan": 0
     */

    val apkLink: String,
    val audit: Int,
    val author: String,
    val canEdit: Boolean,
    val chapterId: Int,
    val chapterName: String,
    val collect: Boolean,
    val courseId: Int,
    val desc: String,
    val descMd: String,
    val envelopePic: String,
    val fresh: Boolean,
    val host: String,
    val id: Int,
    val link: String,
    val niceDate: String,
    val niceShareDate: String,
    val origin: String,
    val prefix: String,
    val projectLink: String,
    val publishTime: Long,
    val realSuperChapterId: Int,
    val selfVisible: Int,
    val shareDate: Long,
    val shareUser: String,
    val superChapterId: Int,
    val superChapterName: String,
    val tags: List<Tag>?,
    val title: String,
    val type: Int,
    val userId: Int,
    val visible: Int,
    val zan: Int
)

data class Tag(
    val name: String,
    val url: String
)