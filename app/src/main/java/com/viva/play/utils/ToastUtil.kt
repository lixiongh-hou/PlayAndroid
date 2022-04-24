package com.viva.play.utils

import android.annotation.SuppressLint
import android.os.Looper
import android.widget.Toast
import androidx.arch.core.executor.ArchTaskExecutor
import com.viva.play.App

/**
 * @author 李雄厚
 *
 * @features ***
 */
object ToastUtil {

    private val sToast by lazy {
        return@lazy Toast.makeText(App.instance, "", Toast.LENGTH_SHORT)
    }

    @SuppressLint("RestrictedApi")
    @JvmStatic
    @JvmOverloads
    fun Any.toast(duration: Int = Toast.LENGTH_SHORT){
        if (Thread.currentThread() != Looper.getMainLooper().thread){
            ArchTaskExecutor.getInstance().postToMainThread{
                sToast.duration = duration
                sToast.setText(this.toString())
                sToast.show()
            }
        }else{
            sToast.duration = duration
            sToast.setText(this.toString())
            sToast.show()
        }
    }
}