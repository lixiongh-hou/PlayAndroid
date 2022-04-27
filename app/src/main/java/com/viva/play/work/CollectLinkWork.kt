package com.viva.play.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.viva.play.App
import com.viva.play.db.BaseDataBase
import com.viva.play.db.entity.PoCollectLinkEntity
import com.viva.play.service.HttpClient
import com.viva.play.utils.CookieCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author 李雄厚
 *
 *
 */
class CollectLinkWork(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    /**
     * 默认执行 Dispatchers.Default
     */
    override suspend fun doWork(): Result {
        if (!CookieCache.isLogin()) {
            return Result.failure()
        }
        return withContext(Dispatchers.IO) {
            try {
                val data = HttpClient.commonService.getCollectLinkList()
                if (data.success()) {
                    if (data.data.isNullOrEmpty()) {
                        Result.failure()
                    } else {
                        BaseDataBase.getInstance(App.instance)
                            .collectDao().deleteLink()
                        BaseDataBase.getInstance(App.instance)
                            .collectDao().insertLink(PoCollectLinkEntity.parse(data.data))
                        Result.success()
                    }
                } else {
                    Result.failure()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (runAttemptCount < 3) {
                    Result.retry()
                } else {
                    Result.failure()
                }
            }
        }


    }
}