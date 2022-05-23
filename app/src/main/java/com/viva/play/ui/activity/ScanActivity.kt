package com.viva.play.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.viva.play.R
import com.viva.play.base.BaseActivity
import com.viva.play.databinding.ActivityScanBinding
import com.viva.play.dialog.ScanDialog
import com.viva.play.dialog.TipDialog
import com.viva.play.utils.PermissionUtil
import com.viva.play.utils.UrlOpenUtils
import com.viva.play.utils.bind.binding
import per.goweii.codex.decorator.beep.BeepDecorator
import per.goweii.codex.decorator.gesture.GestureDecorator
import per.goweii.codex.decorator.vibrate.VibrateDecorator
import per.goweii.codex.processor.mlkit.MLKitScanProcessor
import per.goweii.codex.scanner.CameraProxy
import per.goweii.codex.scanner.CodeScanner

class ScanActivity : BaseActivity() {

    private val binding by binding<ActivityScanBinding>()

    companion object {
        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, ScanActivity::class.java))
            activity.overridePendingTransition(
                R.anim.swipeback_activity_open_bottom_in,
                R.anim.swipeback_activity_open_alpha_out
            )
        }
    }

    private var codeScanner: CodeScanner? = null

    @SuppressLint("MissingPermission")
    private val registerCameraPermissionInfo =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                codeScanner?.startScan()
            } else {
                TipDialog.Builder().apply {
                    title = "申请相机权限"
                    msg = "我们需要申请相机权限才能进行二维码扫描"
                    yesText = "设置"
                    callbackYes = {
                    }
                }
            }
        }

    @SuppressLint("MissingPermission")
    override fun initView(savedInstanceState: Bundle?) {
        binding.ivTorch.setOnClickListener {
            codeScanner?.enableTorch(!binding.ivTorch.isSelected)
        }
        codeScanner = binding.codeScanner.apply {
            cameraProxyLiveData.observe(this@ScanActivity) { cameraProxy ->
                cameraProxy?.torchState?.observe(this@ScanActivity) { torchState ->
                    when (torchState) {
                        CameraProxy.TORCH_ON -> binding.ivTorch.isSelected = true
                        CameraProxy.TORCH_OFF -> binding.ivTorch.isSelected = false
                    }
                }
            }
            addProcessor(MLKitScanProcessor())
            addDecorator(
                binding.frozenView, //成功冻结帧
                binding.finderView, //微信装饰器
                BeepDecorator(), //声音装饰器
                VibrateDecorator(), //震动装饰器
                GestureDecorator() //手势控制装饰器
            )

            onFound {
                onScanQRCodeSuccess(it.first().text)
            }

            bindToLifecycle(this@ScanActivity)
        }
//        if (PermissionUtil.checkPermissionGranted(Manifest.permission.CAMERA)) {
//            codeScanner?.startScan()
//        } else {
//            registerCameraPermissionInfo.launch(Manifest.permission.CAMERA)
//        }
        registerCameraPermissionInfo.launch(Manifest.permission.CAMERA)
    }

    @SuppressLint("MissingPermission")
    private fun onScanQRCodeSuccess(result: String) {
        window.decorView.postDelayed({
            ScanDialog.show(supportFragmentManager, result, object :
                ScanDialog.OnMenuClickListener {
                override fun access(result: String) {
                    UrlOpenUtils.with(result).open(this@ScanActivity)
                }

                override fun copy(result: String) {
                }

                override fun saveText(result: String) {
                }

                override fun share(result: String) {
                }

                override fun dismiss() {
                    codeScanner?.startScan()
                }

            })
        }, 300L)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.swipeback_activity_close_alpha_in,
            R.anim.swipeback_activity_close_bottom_out
        )
    }

}