package com.viva.play.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import com.viva.play.db.entity.PoReadLaterEntity
import com.viva.play.service.ApiError
import com.viva.play.service.doFailure
import com.viva.play.service.doSuccess
import com.viva.play.service.request.CommonRequest

/**
 * @author 李雄厚
 *
 *
 */
open class BaseModel(private val commonRequest: CommonRequest? = null) : ViewModel() {

    fun pagingConfig(
        pageSize: Int = 20,
        placeholders: Boolean = false,
        distance: Int = 5,
        loadSize: Int = 20
    ): PagingConfig = PagingConfig(
        // 每页显示的数据的大小
        pageSize = pageSize,
        // 开启占位符
        enablePlaceholders = placeholders,
        // 预刷新的距离，距离最后一个 item 多远时加载数据
        // 默认为 pageSize
        prefetchDistance = distance,
        // 初始化加载数量，默认为 pageSize * 3
        initialLoadSize = loadSize
    )

    val error = MutableLiveData<ApiError>()

    val _collectArticle = MutableLiveData<Boolean>()
    val collectArticle: LiveData<Boolean>
        get() = _collectArticle


    /**
     * 收藏站内文章
     */
    fun collectArticle(id: Int) {
        commonRequest?.collectArticle(viewModelScope, id) {
            it.doSuccess {
                _collectArticle.postValue(true)
            }
            it.doFailure { apiError ->
                error.postValue(apiError)
            }
        }
    }

    /**
     * 取消收藏站内文章
     */
    fun unCollectArticle(id: Int) {
        commonRequest?.unCollectArticle(viewModelScope, id) {
            it.doSuccess {
                _collectArticle.postValue(false)
            }
            it.doFailure { apiError ->
                error.postValue(apiError)
            }
        }
    }

    /**
     * 是否是已添加稍后阅读
     */
    private val _isReadLater = MutableLiveData<Boolean>()
    val isReadLater: LiveData<Boolean>
        get() = _isReadLater

    fun isReadLater(link: String) {
        commonRequest?.isReadLater(viewModelScope, link) {
            it.doSuccess { success ->
                _isReadLater.postValue(success)
            }
            it.doFailure { apiError ->
                error.postValue(apiError)
            }
        }
    }

    private val _addReadLater = MutableLiveData<Boolean>()
    val addReadLater: LiveData<Boolean>
        get() = _addReadLater

    /**
     * 添加稍后阅读
     */
    fun addReadLater(data: PoReadLaterEntity) {
        commonRequest?.addReadLater(viewModelScope, data) {
            it.doSuccess {
                _addReadLater.postValue(true)
            }
            it.doFailure { apiError ->
                error.postValue(apiError)
            }
        }
    }

    /**
     * 删除稍后阅读
     */
    fun removeReadLater(link: String) {
        commonRequest?.removeReadLater(viewModelScope, link) {
            it.doSuccess {
                _addReadLater.postValue(false)
            }
            it.doFailure { apiError ->
                error.postValue(apiError)
            }
        }
    }

}