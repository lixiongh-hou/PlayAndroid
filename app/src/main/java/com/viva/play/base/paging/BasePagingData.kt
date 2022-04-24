package com.viva.play.base.paging

import androidx.paging.RemoteMediator

/**
 * @author 李雄厚
 *
 * 当只有使用了 paging3 库中的 [RemoteMediator] 类才继承他
 */
abstract class BasePagingData {
    var page: Int = 0
}