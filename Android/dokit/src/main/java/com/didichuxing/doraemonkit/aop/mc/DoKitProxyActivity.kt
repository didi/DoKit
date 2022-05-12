package com.didichuxing.doraemonkit.aop.mc

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.annotation.RequiresApi
import com.didichuxing.doraemonkit.kit.core.DoKitServiceEnum
import com.didichuxing.doraemonkit.kit.core.DoKitServiceManager


/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/11/12-16:02
 * 描    述：用于拦截Activity中的所有事件
 * 修订历史：
 * ================================================
 */
public open class DoKitProxyActivity : Activity() {

    companion object {
        private const val TAG = "DoKitProxyActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DoKitServiceManager.dispatch(DoKitServiceEnum.onCreate, this)
    }


    override fun onStart() {
        super.onStart()
        DoKitServiceManager.dispatch(DoKitServiceEnum.onStart, this)

    }

    override fun onResume() {
        super.onResume()
        DoKitServiceManager.dispatch(DoKitServiceEnum.onResume, this)

    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        DoKitServiceManager.dispatch(DoKitServiceEnum.dispatchTouchEvent, this)
        return super.dispatchTouchEvent(ev)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        DoKitServiceManager.dispatch(
            DoKitServiceEnum.onConfigurationChanged,
            this
        )
    }


//    override fun onBackPressed() {
//        super.onBackPressed()
//        DokitServiceManager.dispatch(DokitServiceEnum.onBackPressed, this)
//    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (applicationInfo.targetSdkVersion < Build.VERSION_CODES.ECLAIR) {
                DoKitServiceManager.dispatch(DoKitServiceEnum.onBackPressed, this)
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {

        if (applicationInfo.targetSdkVersion >= Build.VERSION_CODES.ECLAIR) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event!!.isTracking && !event.isCanceled) {
                DoKitServiceManager.dispatch(DoKitServiceEnum.onBackPressed, this)
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onPause() {
        super.onPause()
        DoKitServiceManager.dispatch(DoKitServiceEnum.onPause, this)

    }

    override fun onStop() {
        super.onStop()
        DoKitServiceManager.dispatch(DoKitServiceEnum.onStop, this)

    }

    override fun onDestroy() {
        super.onDestroy()
        DoKitServiceManager.dispatch(DoKitServiceEnum.onDestroy, this)
    }

    override fun finish() {
        super.finish()
        DoKitServiceManager.dispatch(DoKitServiceEnum.finish, this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        //LogHelper.i(TAG, "===onAttachedToWindow===")

    }

    //真正可见
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
    }



}
