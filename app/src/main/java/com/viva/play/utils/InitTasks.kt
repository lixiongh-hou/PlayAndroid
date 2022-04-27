package com.viva.play.utils

import android.app.Application
import android.util.Log
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.WebView
import com.viva.play.utils.web.utils.PersistentCookieUtils
import com.viva.play.views.HeaderLayout
import per.goweii.swipeback.SwipeBack
import per.goweii.swipeback.SwipeBackDirection
import java.util.LinkedHashMap

/**
 * @author 李雄厚
 *
 *
 */

class SmartRefreshInitTask : SyncInitTask() {
    override fun init(application: Application) {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ ->
            HeaderLayout(context)
        }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
            ClassicsFooter(context)
        }
    }

    override fun onlyMainProcess(): Boolean {
        return true
    }

    override fun level(): Int {
        return 0
    }

}

class CookieManagerInitTask : SyncInitTask() {
    override fun init(application: Application) {
        PersistentCookieUtils.init(application)
    }

    override fun onlyMainProcess(): Boolean {
        return true
    }

    override fun level(): Int {
        return 0
    }
}

class SwipeBackInitTask : SyncInitTask() {
    override fun init(application: Application) {
        SwipeBack.getInstance().init(application)
        SwipeBack.getInstance().swipeBackDirection = SwipeBackDirection.RIGHT
        SwipeBack.getInstance().swipeBackTransformer = null
    }

    override fun onlyMainProcess(): Boolean {
        return true
    }

    override fun level(): Int {
        return 0
    }
}

class X5InitTask : AsyncInitTask() {
    override fun init(application: Application) {
        val map = HashMap<String, Any>()
        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
        map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
        QbSdk.initTbsSettings(map)
       QbSdk.initX5Environment(application, object : QbSdk.PreInitCallback {
            override fun onCoreInitFinished() {
                Log.d("x5", "initX5Environment->onCoreInitFinished")
            }

            override fun onViewInitFinished(b: Boolean) {
                Log.d("x5", "initX5Environment->onViewInitFinished=$b")
            }
        })
    }

    override fun onlyMainProcess(): Boolean {
        return false
    }

    override fun level(): Int {
        return 0
    }
}


class BuglyInitTask : SyncInitTask() {
    override fun init(application: Application) {
        if (DebugUtils.isDebug()) return
        CrashReport.setIsDevelopmentDevice(application, DebugUtils.isDebug())
        val strategy = CrashReport.UserStrategy(application)
        strategy.setCrashHandleCallback(object : CrashReport.CrashHandleCallback() {
            override fun onCrashHandleStart(
                crashType: Int,
                errorType: String,
                errorMessage: String,
                errorStack: String
            ): Map<String, String> {
                val map = LinkedHashMap<String, String>()
                val x5CrashInfo = WebView.getCrashExtraMessage(application)
                map["x5crashInfo"] = x5CrashInfo
                return map
            }

            override fun onCrashHandleStart2GetExtraDatas(
                crashType: Int,
                errorType: String,
                errorMessage: String,
                errorStack: String
            ): ByteArray? {
                return try {
                    "Extra data.".toByteArray(charset("UTF-8"))
                } catch (e: Exception) {
                    null
                }
            }
        })
        strategy.isUploadProcess = true
        CrashReport.initCrashReport(application, "com.viva.play", DebugUtils.isDebug(), strategy)
    }

    override fun onlyMainProcess(): Boolean {
        return false
    }

    override fun level(): Int {
        return 3
    }
}