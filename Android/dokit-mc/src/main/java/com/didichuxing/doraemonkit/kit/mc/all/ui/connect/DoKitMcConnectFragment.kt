package com.didichuxing.doraemonkit.kit.mc.all.ui.connect

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.mc.all.ConnectMode
import com.didichuxing.doraemonkit.kit.mc.all.DoKitMcManager
import com.didichuxing.doraemonkit.kit.mc.all.DokitMcConnectManager
import com.didichuxing.doraemonkit.kit.mc.all.ui.DoKitMcActivity
import com.didichuxing.doraemonkit.kit.mc.all.ui.McClientHistory
import com.didichuxing.doraemonkit.kit.mc.net.DoKitMcConnectClient
import com.didichuxing.doraemonkit.mc.R
import com.didichuxing.doraemonkit.util.ToastUtils
import kotlinx.coroutines.launch

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/10-10:52
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DoKitMcConnectFragment : BaseFragment() {

    private var history: McClientHistory? = null

    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_mc_connect
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        history = DokitMcConnectManager.itemHistory
        if (history != null) {
            findViewById<TextView>(R.id.tv_host_info).text = "当前设备已连接主机:【${history?.host}】"
            findViewById<TextView>(R.id.info).text = "ws://${history?.host}:${history?.port}/${history?.path}"
        } else {
            findViewById<TextView>(R.id.tv_host_info).text = "当前设备已连接主机:--"
            findViewById<TextView>(R.id.info).text = "ws://--"
        }
        val btb = findViewById<View>(R.id.btn_close)

        history.let {
            btb.isEnabled = it!!.enable
        }

        findViewById<View>(R.id.btn_close).setOnClickListener {
            lifecycleScope.launch {
                if (DokitMcConnectManager.connectMode == ConnectMode.CLOSE) {
                    ToastUtils.showShort("未开启联网模式")

                } else {
                    DoKit.removeFloating(ConnectDokitView::class)
                    DoKitMcManager.CONNECT_MODE = WSMode.UNKNOW
                    DokitMcConnectManager.currentConnectHistory = null
                    DoKitMcConnectClient.close()
                    if (activity is DoKitMcActivity) {
                        (activity as DoKitMcActivity).onBackPressed()
                    }
                }
            }
        }
    }


}
