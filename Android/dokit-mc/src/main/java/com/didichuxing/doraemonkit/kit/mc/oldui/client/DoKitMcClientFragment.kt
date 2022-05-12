package com.didichuxing.doraemonkit.kit.mc.oldui.client

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.kit.test.TestMode
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.mc.oldui.DoKitMcManager
import com.didichuxing.doraemonkit.kit.mc.ui.DoKitMcActivity
import com.didichuxing.doraemonkit.kit.mc.ui.adapter.McClientHistory
import com.didichuxing.doraemonkit.kit.mc.net.DoKitMcClient
import com.didichuxing.doraemonkit.kit.mc.net.DokitMcConnectManager
import com.didichuxing.doraemonkit.kit.mc.ui.McPages
import com.didichuxing.doraemonkit.mc.R
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
class DoKitMcClientFragment : BaseFragment() {
    private var history: McClientHistory? = null

    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_mc_client
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        history = DokitMcConnectManager.itemHistory

        findViewById<TextView>(R.id.tv_host_info).text =
            "当前设备已连接主机:ws://${history?.host}:${history?.port}/${history?.path}"
        findViewById<View>(R.id.btn_close).setOnClickListener {
            lifecycleScope.launch {
                DoKitMcManager.WS_MODE = TestMode.UNKNOWN
                DoKit.removeFloating(ClientDoKitView::class)
                DoKitMcClient.close()
                if (activity is DoKitMcActivity) {
                    (activity as DoKitMcActivity).onBackPressed()
                }
            }
        }

        findViewById<View>(R.id.btn_history).setOnClickListener {
            lifecycleScope.launch {
                if (activity is DoKitMcActivity) {
                    (activity as DoKitMcActivity).pushFragment(McPages.CLIENT_HISTORY)
                }
            }
        }

    }


}
