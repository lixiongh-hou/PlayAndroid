package com.viva.play.ui.model

import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.viva.play.base.BaseModel
import com.viva.play.db.entity.PoArticleEntity
import com.viva.play.service.ApiError
import com.viva.play.service.doFailure
import com.viva.play.service.doSuccess
import com.viva.play.service.entity.HomeEntity
import com.viva.play.service.request.CommonRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author 李雄厚
 *
 *
 */
@HiltViewModel
class HomeModel @Inject constructor(
    private val commonRequest: CommonRequest
) : BaseModel(commonRequest) {

    companion object {
        private const val VISIBLE_THRESHOLD = 4
    }

    // 避免同时触发多个请求
    private var isRequestInProgress = false

    private val _homeData = MutableLiveData<HomeEntity>()
    val homeData: LiveData<HomeEntity>
        get() = _homeData

    private val _article = MutableLiveData<List<PoArticleEntity>>()
    val article: LiveData<List<PoArticleEntity>>
        get() = _article

    private val _loadError = MutableLiveData<ApiError>()
    val loadError: LiveData<ApiError>
        get() = _loadError

    val url = ObservableField("")
    val title = ObservableField("")
    val id = ObservableInt(-1)
    val collected = ObservableBoolean(false)
    val author = ObservableField("")
    val userId = ObservableInt(-1)
    val collectId = ObservableInt(-1)

    fun getHomeData(refresh: Boolean = false, local: Boolean = false) {
        if (!refresh) {
            if (_homeData.value != null) {
                return
            }
        }
        isRequestInProgress = true
        commonRequest.getHomeData(viewModelScope, local) {
            it.doSuccess { success ->
                _homeData.postValue(success)
                isRequestInProgress = false

            }
            it.doFailure { apiError ->
                error.postValue(apiError)
            }
        }
    }

    fun getArticle(page: Int = 0) {
        isRequestInProgress = true
        commonRequest.getArticle(viewModelScope, page) {
            it.doSuccess { success ->
                _article.postValue(success)
                isRequestInProgress = false
            }
            it.doFailure { apiError ->
                _loadError.postValue(apiError)
            }
        }
    }


    /**
     * 通过滑动来无缝加载更多数据
     * 这里是自己写的没用到谷歌的【paging】
     * 因为【paging】这个库在刷新数据的时候RecyclerView闪动的幅度较大，目前没找到好方法解决
     */
    fun listScrolled(
        visibleItemCount: Int,
        lastVisibleItemPosition: Int,
        totalItemCount: Int,
        loadMore: () -> Unit,
    ) {
        if (visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount) {
            if (isRequestInProgress) return
            loadMore.invoke()
            Log.e(
                "TAG", "加载+visibleItemCount--$visibleItemCount" +
                        "---lastVisibleItemPosition---$lastVisibleItemPosition--" +
                        "totalItemCount---$totalItemCount"
            )
        }
    }
}