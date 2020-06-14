package com.didichuxing.doraemonkit.kit.dbdebug

import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TextView
import com.amitshekhar.DebugDB
import com.amitshekhar.debug.encrypt.sqlite.DebugDBEncryptFactory
import com.amitshekhar.debug.sqlite.DebugDBFactory
import com.blankj.utilcode.util.NetworkUtils
import com.didichuxing.doraemonkit.DoraemonKit
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar

/**
 * @author jintai
 * Created by jintai on 2019/10/17.
 * 数据库远程调试介绍页面
 */
class DbDebugFragment : BaseFragment() {
    private var tvIp: TextView? = null
    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_db_debug
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            initView()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(Exception::class)
    private fun initView() {
        if (!DebugDB.isServerRunning()) {
            DebugDB.initialize(DoraemonKit.APPLICATION, DebugDBFactory())
            DebugDB.initialize(DoraemonKit.APPLICATION, DebugDBEncryptFactory())
        }
        val titleBar = findViewById<HomeTitleBar>(R.id.title_bar)
        titleBar.setListener(object : HomeTitleBar.OnTitleBarClickListener {
            override fun onRightClick() {
                finish()
            }
        })
        val tvTip = findViewById<TextView>(R.id.tv_tip)
        tvTip.text = Html.fromHtml(resources.getString(R.string.dk_kit_db_debug_desc))
        tvIp = findViewById(R.id.tv_ip)
        if (DebugDB.isServerRunning()) {
            tvIp?.text = DebugDB.getAddressLog().replace("Open ", "").replace("in your browser", "")
        } else {
            tvIp?.text = "servse is not start"
        }
    }

    /**
     * 网络变化时调用
     *
     * @param networkType
     */
    fun networkChanged(networkType: NetworkUtils.NetworkType) {
        if (tvIp == null) {
            return
        }
        if (networkType == NetworkUtils.NetworkType.NETWORK_NO) {
            tvIp?.text = "please check network is connected"
        } else {
            tvIp?.text = DebugDB.getAddressLog().replace("Open ", "").replace("in your browser", "")
        }
    }
}