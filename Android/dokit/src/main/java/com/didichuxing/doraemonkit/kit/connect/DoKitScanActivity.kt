package com.didichuxing.doraemonkit.kit.connect

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar
import com.didichuxing.doraemonkit.zxing.activity.CaptureActivity

/**
 * didi Create on 2022/4/12 .
 *
 * Copyright (c) 2022/4/12 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/12 6:07 下午
 * @Description 用一句话说明文件功能
 */
class DoKitScanActivity : CaptureActivity() {

    companion object {
        private const val REQUEST_CODE = 200999
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkCameraPermission()
    }
    override fun setContentView(@IdRes layoutResID: Int) {
        super.setContentView(layoutResID)
        initTitleBar()
    }

    private fun initTitleBar() {
        val homeTitleBar = HomeTitleBar(this)
        homeTitleBar.setBackgroundColor(resources.getColor(R.color.foreground_wtf))
        homeTitleBar.setIcon(R.mipmap.dk_close_icon)
        homeTitleBar.setListener { finish() }
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            resources.getDimension(R.dimen.dk_home_title_height).toInt()
        )
        (findViewById<View>(android.R.id.content) as FrameLayout).addView(homeTitleBar, params)
    }

    private fun checkCameraPermission() {
        val hasCameraPermission: Int = ContextCompat.checkSelfPermission(application, Manifest.permission.CAMERA)
        if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {

        }
    }
}
