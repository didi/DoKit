package com.didichuxing.doraemonkit.kit.mc.client

import android.view.View
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.constant.CachesKey
import com.didichuxing.doraemonkit.constant.WSEType
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.extension.doKitGlobalScope
import com.didichuxing.doraemonkit.kit.mc.all.WSEvent
import com.didichuxing.doraemonkit.kit.mc.all.ui.BorderDoKitView
import com.didichuxing.doraemonkit.kit.mc.all.view_info.ViewC12c
import com.didichuxing.doraemonkit.util.ActivityUtils
import com.didichuxing.doraemonkit.util.DoKitImageUtil
import com.didichuxing.doraemonkit.util.ScreenUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.Exception

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/9/2-11:05
 * 描    述：从机同步执行失败
 * 修订历史：
 * ================================================
 */
class ClientSyncFailedImpl : ClientSyncFailedListener {
    override fun callBack(type: ClientSyncFailType, viewC12c: ViewC12c?, view: View?) {
        when (type) {
            //页面不一致
            ClientSyncFailType.ACTIVITY -> {

            }
            //找不到对应的控件
            ClientSyncFailType.VIEW -> {

            }
            //模拟点击失败
            ClientSyncFailType.PERFORM_CLICK -> {
                view?.let {
                    doKitGlobalScope.launch {
                        saveScreenShot(view)
                    }
                }

            }
            //模拟获取焦点失败
            ClientSyncFailType.PERFORM_FOCUSED -> {
                view?.let {
                    doKitGlobalScope.launch {
                        saveScreenShot(view)
                    }
                }

            }
        }
    }

    /**
     * 回传执行失败的通用事件
     */
    private suspend fun saveScreenShot(view: View) {
        DoKit.launchFloating(BorderDoKitView::class.java)
        val borderDoKitView =
            DoKit.getDoKitView(
                ActivityUtils.getTopActivity(),
                BorderDoKitView::class.java
            ) as BorderDoKitView
        borderDoKitView.showBorder(view)
        withContext(Dispatchers.IO) {
            delay(200)
            val bitmap = ScreenUtils.screenShot(ActivityUtils.getTopActivity())
            val output = File(getMcShotDir(), "syncFail.png")
            DoKitImageUtil.bitmap2File(bitmap, 100, output)
        }

    }

    /**
     * 获取一机多控截图路径
     */
    private fun getMcShotDir(): File {
        val dir =
            File(ActivityUtils.getTopActivity().cacheDir.toString() + File.separator + CachesKey.MC_SHOT_HISTORY)
        if (!dir.exists()) {
            dir.mkdir()
        }
        return dir
    }


    private fun showFailDialog() {

    }
}
