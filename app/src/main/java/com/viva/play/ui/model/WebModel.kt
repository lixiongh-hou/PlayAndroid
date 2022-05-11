package com.viva.play.ui.model

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.viva.play.base.BaseModel
import com.viva.play.db.entity.PoCollectLinkEntity
import com.viva.play.db.entity.PoReadLaterEntity
import com.viva.play.service.doFailure
import com.viva.play.service.doSuccess
import com.viva.play.service.request.CommonRequest
import com.viva.play.utils.web.WebHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author 李雄厚
 *
 *
 */
@HiltViewModel
class WebModel @Inject constructor(
    private val commonRequest: CommonRequest
) : BaseModel(commonRequest) {

    val url = ObservableField("")
    val title = ObservableField("")
    val id = ObservableInt(-1)
    val collected = ObservableBoolean(false)
    val author = ObservableField("")
    val userId = ObservableInt(-1)

    private val _collectLink = MutableLiveData<Boolean>()
    val collectLink: LiveData<Boolean>
        get() = _collectLink

    private val _collectLinkList = MutableLiveData<List<PoCollectLinkEntity>>()
    val collectLinkList: LiveData<List<PoCollectLinkEntity>>
        get() = _collectLinkList

    /**
     * 获取本地缓存的网址来判断用户有没有收藏过网址
     */
    fun getCollectLinkList() {
        commonRequest.getCollectLinkList(viewModelScope, true) {
            it.doSuccess { success ->
                success.forEach { entity ->
                    addCollected(entity.url)
                    addCollectId(
                        PoCollectLinkEntity(
                            url = entity.url,
                            collectId = entity.collectId
                        )
                    )
                }
                _collectLinkList.postValue(success)
            }
        }
    }

    /**
     * 获取本地缓存的网址来判断用户有没有添加稍后阅读
     */
    fun getReadLaterListData(): LiveData<List<PoReadLaterEntity>> {
        return commonRequest.getReadLaterListLiveData()
    }


    /**
     * 这里的收藏要区分收藏站内文章还是网址
     */
    fun collect(mWebHolder: WebHolder) {
        val url = mWebHolder.getUrl()
        val title = mWebHolder.getTitle()
        if (id.get() == -1) {
            //收藏网址
            commonRequest.collectLink(viewModelScope, title, url) {
                it.doSuccess { success ->
                    if (success != null) {
                        addCollectId(PoCollectLinkEntity(url = url, collectId = success.id))
                    }
                    addCollected(url)
                    _collectLink.postValue(true)
                }
                it.doFailure { apiError ->
                    error.postValue(apiError)
                }
            }
        } else {
            //收藏站内文章
            commonRequest.collectArticle(viewModelScope, id.get()) {
                it.doSuccess {
                    addCollected(url)
                    _collectArticle.postValue(true)
                }
                it.doFailure { apiError ->
                    error.postValue(apiError)
                }
            }
        }
    }

    fun unCollect(mWebHolder: WebHolder) {
        val url = mWebHolder.getUrl()
        if (id.get() == -1) {
            //删除网址
            commonRequest.unCollectLink(viewModelScope, findCollectId(url) ?: -1) {
                it.doSuccess {
                    deleteCollected(url)
                    _collectLink.postValue(true)
                }
                it.doFailure { apiError ->
                    error.postValue(apiError)
                }
            }
        } else {
            //取消收藏
            commonRequest.unCollectArticle(viewModelScope, id.get()) {
                it.doSuccess {
                    deleteCollected(url)
                    _collectArticle.postValue(false)
                }
                it.doFailure { apiError ->
                    error.postValue(apiError)
                }
            }
        }
    }

    /**
     * 添加阅读记录
     */
    fun addReadRecord(
        id: Int,
        author: String,
        userId: Int,
        link: String,
        title: String,
        percent: Float,
        key: String?,
    ) {
        commonRequest.addReadRecord(viewModelScope, id, author, userId, link, title, percent, key) {
            it.doSuccess {

            }
        }
    }

    fun updateReadRecordPercent(
        id: Int,
        key: String,
        link: String,
        percent: Float,
    ) {
        commonRequest.updateReadRecordPercent(viewModelScope, id, key, link, percent) {
            it.doSuccess {

            }
        }
    }


    /*----------------处理是否要收藏网址数据，不会合api合本地数据有关，临时缓存----------------*/
    private val mCollectedList = mutableListOf<String>()
    private val collectId = mutableListOf<PoCollectLinkEntity>()

    val mReadLaterList = mutableListOf<String>()

    /**
     * 用来判断红心是否选中
     */
    fun addCollected(url: String) {
        if (findCollected(url) != null) return
        mCollectedList.add(url)
    }

    /**
     * 删除网址需要
     */
    fun addCollectId(entity: PoCollectLinkEntity) {
        if (findCollectId(entity.url) != null) return
        collectId.add(entity)
    }

    private fun findCollectId(url: String): Int? {
        val data = collectId.filter { it.url == url }
        return if (data.isEmpty()) {
            null
        } else {
            data[0].collectId
        }
    }

    private fun deleteCollected(url: String) {
        mCollectedList.remove(url)
    }

    fun findCollected(url: String): String? {
        val data = mCollectedList.filter { it == url }
        return if (data.isEmpty()) {
            null
        } else {
            data[0]
        }
    }

    fun findReadLater(url: String): String? {
        val data = mReadLaterList.filter { it == url }
        return if (data.isEmpty()) {
            null
        } else {
            data[0]
        }
    }
}