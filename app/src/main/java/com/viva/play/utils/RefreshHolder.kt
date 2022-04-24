package com.viva.play.utils

import com.viva.play.di.MadeInPoetry
import com.viva.play.service.CommonService
import com.viva.play.service.DataConvert.convertNetworkError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * @author 李雄厚
 *
 *
 */
object RefreshHolder {

    private var mCache: String? = null

    suspend fun request(poetryService: CommonService) {
        return withContext(Dispatchers.IO) {
            try {
                val data = poetryService.getPoetry()
                mCache = data.data.content
            } catch (e: Exception) {
                e.printStackTrace()
                e.convertNetworkError {
                }
            }
        }
    }

    suspend fun refresh(poetryService: CommonService) {
        request(poetryService)
    }

    fun get(): String? = mCache


}
