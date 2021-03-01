package com.didichuxing.doraemonkit.aop.mc

import android.app.Activity
import android.content.res.Configuration
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import androidx.annotation.RequiresApi
import com.didichuxing.doraemonkit.kit.core.DoKitActivityOverrideEnum
import com.didichuxing.doraemonkit.kit.core.DoKitActivityOverrideManager
import com.didichuxing.doraemonkit.util.LogHelper
import java.util.logging.Logger


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
        DoKitActivityOverrideManager.dispatch(DoKitActivityOverrideEnum.onCreate, this)
    }


    override fun onStart() {
        super.onStart()
        DoKitActivityOverrideManager.dispatch(DoKitActivityOverrideEnum.onStart, this)

    }

    override fun onResume() {
        super.onResume()
        DoKitActivityOverrideManager.dispatch(DoKitActivityOverrideEnum.onResume, this)

    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        DoKitActivityOverrideManager.dispatch(DoKitActivityOverrideEnum.dispatchTouchEvent, this)


        return super.dispatchTouchEvent(ev)

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        DoKitActivityOverrideManager.dispatch(
            DoKitActivityOverrideEnum.onConfigurationChanged,
            this
        )
    }


    override fun onBackPressed() {
        super.onBackPressed()
        DoKitActivityOverrideManager.dispatch(DoKitActivityOverrideEnum.onBackPressed, this)
    }

    override fun onPause() {
        super.onPause()
        DoKitActivityOverrideManager.dispatch(DoKitActivityOverrideEnum.onPause, this)

    }

    override fun onStop() {
        super.onStop()
        DoKitActivityOverrideManager.dispatch(DoKitActivityOverrideEnum.onStop, this)

    }

    override fun onDestroy() {
        super.onDestroy()
        DoKitActivityOverrideManager.dispatch(DoKitActivityOverrideEnum.onDestroy, this)
    }

    override fun finish() {
        super.finish()
        DoKitActivityOverrideManager.dispatch(DoKitActivityOverrideEnum.finish, this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        //LogHelper.i(TAG, "===onAttachedToWindow===")

    }


}