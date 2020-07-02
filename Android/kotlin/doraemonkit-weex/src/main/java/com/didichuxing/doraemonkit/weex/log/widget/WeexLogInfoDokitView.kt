package com.didichuxing.doraemonkit.weex.log.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.didichuxing.doraemonkit.kit.loginfo.LogInfoDokitView
import com.didichuxing.doraemonkit.kit.loginfo.LogInfoManager
import com.didichuxing.doraemonkit.kit.loginfo.LogLine
import com.didichuxing.doraemonkit.weex.R
import com.didichuxing.doraemonkit.widget.titlebar.LogTitleBar
import org.apache.weex.utils.WXLogUtils.WEEX_TAG

/**
 * Transformed by alvince on 2020/6/30
 *
 * @author haojianglong
 * @date 2019-06-25
 */
class WeexLogInfoDokitView : LogInfoDokitView() {

    override fun onCreateView(context: Context, rootView: FrameLayout): View =
        LayoutInflater.from(context).inflate(R.layout.dk_weex_float_log_info, null)

    override fun onViewCreated(rootView: FrameLayout) {
        super.onViewCreated(rootView)
        findViewById<LogTitleBar>(R.id.dokit_title_bar)
            .setListener(object : LogTitleBar.OnTitleBarClickListener {
                override fun onRightClick() {
                    LogInfoManager.also {
                        //关闭日志服务
                        it.stop()
                        //清空回调
                        it.removeListener()
                        detach()
                    }
                }

                override fun onLeftClick() {
                    minimize()
                }
            })
    }

    override fun onLogCatch(logLines: List<LogLine>) {
        logLines.takeIf { it.isNotEmpty() }
            ?.filter { line ->
                line.tag?.contains(WEEX_TAG) == true
            }
            ?.also {
                super.onLogCatch(it)
            }
    }

}
