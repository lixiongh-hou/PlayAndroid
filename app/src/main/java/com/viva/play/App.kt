package com.viva.play

import android.app.Application
import androidx.work.*
import com.tencent.sonic.sdk.SonicConfig
import com.tencent.sonic.sdk.SonicEngine
import com.viva.play.utils.*
import com.viva.play.utils.web.WebInstance
import com.viva.play.work.CollectLinkWork
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit


/**
 * @author 李雄厚
 *
 *
 */
@HiltAndroidApp
class App : Application() {

    companion object {
        lateinit var instance: App

        fun initDarkMode() {
            NightModeUtils.initNightMode()
        }

        fun notification(){
            // 约束条件
            val constraints = Constraints.Builder()
                //触发时网络状态
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val workRequest = OneTimeWorkRequestBuilder<CollectLinkWork>()
                //5秒后执行，当然你也可以用此方法指定单位(毫秒/秒/分钟/小时/天)
                .setInitialDelay(3, TimeUnit.SECONDS)
                //设置重新执行任务
                //第一个参数：BackoffPolicy有两个值：
                //BackoffPolicy.LINEAR(每次重试的时间线性增加，比如第一次10秒，第二次就是20秒)
                //BackoffPolicy.EXPONENTIAL(每次重试时间指数增加)。
                .setBackoffCriteria(BackoffPolicy.LINEAR, 5, TimeUnit.SECONDS)
                //约束条件
                .setConstraints(constraints)
                .build()
            val workManager = WorkManager.getInstance(instance)
            workManager.enqueue(workRequest)
        }
    }


    override fun onCreate() {
        super.onCreate()
        instance = this
        initDarkMode()
        if (!SonicEngine.isGetInstanceAllowed()) {
            SonicEngine.createInstance(TTPRuntime(this), SonicConfig.Builder().build())
        }
        FlowBusInitializer.init(this)

        InitTaskRunner(instance)
            .add(SmartRefreshInitTask())
            .add(CookieManagerInitTask())
            .add(BuglyInitTask())
//            .add(SwipeBackInitTask())
            .add(X5InitTask())
            .run()
        WebInstance.getInstance(instance).create()
        notification()
    }


}