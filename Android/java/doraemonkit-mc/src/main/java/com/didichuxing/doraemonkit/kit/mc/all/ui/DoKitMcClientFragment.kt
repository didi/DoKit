package com.didichuxing.doraemonkit.kit.mc.all.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.didichuxing.doraemonkit.constant.DoKitConstant
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.core.SimpleDokitStarter
import com.didichuxing.doraemonkit.kit.mc.all.McConstant
import com.didichuxing.doraemonkit.kit.mc.client.ClientDokitView
import com.didichuxing.doraemonkit.kit.mc.client.DoKitWsClient
import com.didichuxing.doraemonkit.kit.mc.server.HostDokitView
import com.didichuxing.doraemonkit.mc.R

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/10-10:52
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DoKitMcClientFragment : BaseFragment() {

    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_mc_client
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findViewById<TextView>(R.id.tv_host_info).text =
            "当前设备已连接主机:【${McConstant.HOST_INFO?.deviceName}】"
        findViewById<View>(R.id.btn_close).setOnClickListener {
            DoKitWsClient.close()
            if (activity is DoKitMcActivity) {
                (activity as DoKitMcActivity).changeFragment(WSMode.UNKNOW)
            }
        }
        //启动悬浮窗
        SimpleDokitStarter.startFloating(ClientDokitView::class.java)
    }


}