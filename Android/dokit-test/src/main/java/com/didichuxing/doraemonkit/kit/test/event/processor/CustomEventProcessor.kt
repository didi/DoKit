package com.didichuxing.doraemonkit.kit.test.event.processor

import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.kit.test.event.WSClientProcessor
import com.didichuxing.doraemonkit.kit.test.event.ControlEvent
import com.didichuxing.doraemonkit.kit.test.util.McXposedHookUtils
import com.didichuxing.doraemonkit.kit.test.util.ViewPathUtil
import com.didichuxing.doraemonkit.kit.test.util.WindowPathUtil
import com.didichuxing.doraemonkit.util.*

object CustomEventProcessor {


    fun onCustomEvent(wsEvent: ControlEvent) {
        if (DoKitManager.MC_CLIENT_PROCESSOR == null) {
            LogHelper.e(WSClientProcessor.TAG, "client processor 从机处理器不存在,请重新启动从机")
            return
        }

        wsEvent.viewC12c?.let { viewC12c ->
            //假如没有控件信息执行自定义事件
            if (viewC12c.viewPaths == null) {
                DoKitManager.MC_CLIENT_PROCESSOR?.process(
                    ActivityUtils.getTopActivity(),
                    null,
                    viewC12c.actionName,
                    viewC12c.params
                )
                return
            }

            if (McXposedHookUtils.ROOT_VIEWS == null || viewC12c.windowIndex == -1) {
                LogHelper.e(WSClientProcessor.TAG, "匹配控件失败，请手动操作 wsEvent===>$wsEvent")
                ToastUtils.showShort("匹配控件失败，请手动操作")
                return
            }
            var viewRootImpl: ViewParent? = WindowPathUtil.findViewRoot(McXposedHookUtils.ROOT_VIEWS, viewC12c.windowIndex)
            viewRootImpl?.let {
                val decorView: ViewGroup =
                    ReflectUtils.reflect(it).field("mView").get<View>() as ViewGroup
                val targetView: View? =
                    ViewPathUtil.findViewByViewParentInfo(decorView, viewC12c.viewPaths)
                targetView?.let { target ->
                    //执行自定义事件
                    DoKitManager.MC_CLIENT_PROCESSOR?.process(
                        ActivityUtils.getTopActivity(),
                        target,
                        viewC12c.actionName,
                        viewC12c.params
                    )
                } ?: run {
                    //执行自定义事件
                    DoKitManager.MC_CLIENT_PROCESSOR?.process(
                        ActivityUtils.getTopActivity(),
                        null,
                        viewC12c.actionName,
                        viewC12c.params
                    )
                    ToastUtils.showShort("匹配控件失败，请手动操作")
                }
            } ?: run {
                //执行自定义事件
                DoKitManager.MC_CLIENT_PROCESSOR?.process(
                    ActivityUtils.getTopActivity(),
                    null,
                    viewC12c.actionName,
                    viewC12c.params

                )
                ToastUtils.showShort("无法确定当前控件所属窗口")
            }

        } ?: run { ToastUtils.showShort("解析长连接数据失败") }

    }

}
