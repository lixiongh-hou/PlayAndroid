package com.viva.play.service.entity

import com.viva.play.db.entity.PoArticleEntity
import com.viva.play.db.entity.PoBannerEntity

/**
 * @author 李雄厚
 *
 * 首页数据实体类
 * 数据是由 Banner+置顶文章+首页文章三个接口组合而来
 */
data class HomeEntity(
    val banner: List<PoBannerEntity>,
    val article: List<PoArticleEntity>
) {
    //这边处理一下，把banner图放进Rv里面，第一个Position
    fun getHomeData(): List<PoArticleEntity> {
        val data = mutableListOf<PoArticleEntity>()
        data.add(
            PoArticleEntity(
                "",
                0,
                "",
                false,
                0,
                "",
                "",
                false,
                0,
                "",
                "",
                "",
                "",
                0L,
                0,
                0,
                0L,
                "",
                0,
                "",
                null,
                "",
                0,
                0
            ).apply {
                banner = this@HomeEntity.banner
            }
        )
        data.addAll(article)
        return data
    }

    companion object{

    }
}