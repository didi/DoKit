package com.didichuxing.doraemonkit.kit.mc.client

import android.os.Bundle
import android.view.View
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.constant.CachesKey
import com.didichuxing.doraemonkit.extension.doKitGlobalScope
import com.didichuxing.doraemonkit.kit.mc.all.ui.BorderDoKitView
import com.didichuxing.doraemonkit.kit.mc.all.ui.McDialogDoKitView
import com.didichuxing.doraemonkit.kit.mc.all.view_info.ViewC12c
import com.didichuxing.doraemonkit.util.ActivityUtils
import com.didichuxing.doraemonkit.util.DoKitImageUtil
import com.didichuxing.doraemonkit.util.ScreenUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/9/2-11:05
 * 描    述：从机同步执行失败
 * 修订历史：
 * ================================================
 */
class ClientSyncFailedImpl : OnClientSyncFailedListener {

    override fun onActivityNotSync() {
        showFailDialog("当前页面与主机不一致")
    }

    override fun onViewNotFound(viewC12c: ViewC12c) {
        showFailDialog("当前页面无法找到与主机对应的view")
    }

    override fun onViewPerformClickFailed(viewC12c: ViewC12c, view: View) {
        doKitGlobalScope.launch {
            saveScreenShot(view,"当前页面与主机对应的view执行点击事件失败")
        }
    }

    override fun onViewPerformLongClickFailed(viewC12c: ViewC12c, view: View) {
        doKitGlobalScope.launch {
            saveScreenShot(view,"当前页面与主机对应的view执行长按事件失败")
        }
    }

    override fun onViewPerformFocusedFailed(viewC12c: ViewC12c, view: View) {
        doKitGlobalScope.launch {
            saveScreenShot(view,"当前页面与主机对应的view执行焦点获取失败")
        }
    }


    /**
     * 回传执行失败的通用事件
     */
    private suspend fun saveScreenShot(view: View,exception: String) {
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

        showFailDialog(exception)

    }

    /**
     * 获取一机多控截图文件夹路径
     */
    private fun getMcShotDir(): File {
        val dir =
            File(ActivityUtils.getTopActivity().cacheDir.toString() + File.separator + CachesKey.MC_SHOT_HISTORY)
        if (!dir.exists()) {
            dir.mkdir()
        }
        return dir
    }


    private fun showFailDialog(exception: String) {
        val bundle = Bundle()
        bundle.putString("text", exception)
        DoKit.launchFloating<McDialogDoKitView>(bundle = bundle)
    }

}
