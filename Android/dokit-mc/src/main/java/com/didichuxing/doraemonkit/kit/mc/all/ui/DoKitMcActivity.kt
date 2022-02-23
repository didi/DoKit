package com.didichuxing.doraemonkit.kit.mc.all.ui

import android.os.Bundle
import android.text.TextUtils
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.kit.core.BaseActivity
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.mc.all.ui.client.DoKitMcClientFragment
import com.didichuxing.doraemonkit.kit.mc.all.ui.client.DoKitMcClientHistoryFragment
import com.didichuxing.doraemonkit.kit.mc.all.ui.connect.DoKitMcConnectFragment
import com.didichuxing.doraemonkit.kit.mc.all.ui.connect.DoKitMcConnectHistoryFragment
import com.didichuxing.doraemonkit.kit.mc.all.ui.host.DoKitMcHostFragment
import com.didichuxing.doraemonkit.kit.mc.all.ui.record.DoKitMcDatasFragment
import com.didichuxing.doraemonkit.mc.R
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/10-10:52
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DoKitMcActivity : BaseActivity() {
    private lateinit var mTitlebar: HomeTitleBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dk_activity_mc)

        mTitlebar = findViewById(R.id.title_bar)
        mTitlebar.setListener {
            onBackPressed()
        }

        val mode: String? = intent?.getStringExtra("WS_MODE_ORDINAL")
        if (TextUtils.isEmpty(mode)) {
            newHomeFragment()
        } else {
            mode?.let {
                val vs: WSMode = WSMode.valueOf(it)
                changeFragment(vs)
            }
        }
    }

    fun newHomeFragment() {
        changeFragment(WSMode.UNKNOW)
    }

    fun changeFragment(wsMode: WSMode) {
        changeFragment(wsMode, false)
    }

    fun pushFragment(wsMode: WSMode) {
        changeFragment(wsMode, true)
    }

    fun changeFragment(wsMode: WSMode, push: Boolean) {
        val fragment: BaseFragment = when (wsMode) {
            WSMode.RECORDING,
            WSMode.UNKNOW -> {
                mTitlebar.setTitle("一机多控（主页）")
                DoKitMcMainFragment()
            }
            WSMode.CONNECT -> {
                mTitlebar.setTitle("一机多控（联网）")
                DoKitMcConnectFragment()
            }
            WSMode.CONNECT_HISTORY -> {
                mTitlebar.setTitle("一机多控（联网历史）")
                DoKitMcConnectHistoryFragment()
            }
            WSMode.HOST -> {
                mTitlebar.setTitle("一机多控（主机）")
                DoKitMcHostFragment()
            }
            WSMode.CLIENT -> {
                mTitlebar.setTitle("一机多控（从机）")
                DoKitMcClientFragment()
            }

            WSMode.CLIENT_HISTORY -> {
                mTitlebar.setTitle("一机多控（从机历史）")
                DoKitMcClientHistoryFragment()
            }

            WSMode.MC_CASELIST -> {
                mTitlebar.setTitle("一机多控（用例列表）")
                DoKitMcDatasFragment()
            }
            else -> {
                mTitlebar.setTitle("一机多控（主页）")
                DoKitMcMainFragment()
            }
        }

        if (push) {
            showContent(R.id.fragment_container_view, fragment)
        } else {
            replaceContent(R.id.fragment_container_view, fragment)
        }

    }

}
