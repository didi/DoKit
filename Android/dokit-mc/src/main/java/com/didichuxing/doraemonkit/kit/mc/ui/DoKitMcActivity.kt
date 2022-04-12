package com.didichuxing.doraemonkit.kit.mc.ui

import android.os.Bundle
import android.text.TextUtils
import com.didichuxing.doraemonkit.kit.core.BaseActivity
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.mc.ui.client.DoKitMcClientFragment
import com.didichuxing.doraemonkit.kit.mc.ui.client.DoKitMcClientHistoryFragment
import com.didichuxing.doraemonkit.kit.mc.ui.connect.DoKitMcConnectFragment
import com.didichuxing.doraemonkit.kit.mc.ui.connect.DoKitMcConnectHistoryFragment
import com.didichuxing.doraemonkit.kit.mc.ui.host.DoKitMcHostFragment
import com.didichuxing.doraemonkit.kit.mc.ui.main.DoKitMcMainFragment
import com.didichuxing.doraemonkit.kit.mc.ui.record.DoKitMcDatasFragment
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
                val vs: McPages = McPages.valueOf(it)
                changeFragment(vs)
            }
        }
    }


    fun changeFragment(wsMode: McPages) {
        changeFragment(wsMode, false)
    }

    fun pushFragment(wsMode: McPages) {
        changeFragment(wsMode, true)
    }

    private fun newHomeFragment() {
        changeFragment(McPages.CONNECT_HISTORY)
    }


    private fun changeFragment(wsMode: McPages, push: Boolean) {
        val fragment: BaseFragment = when (wsMode) {
            McPages.MAIN -> {
                mTitlebar.setTitle("一机多控（原)")
                DoKitMcMainFragment()
            }
            McPages.RECORDING,
            McPages.UNKNOW -> {
                mTitlebar.setTitle("一机多控（原）")
                DoKitMcMainFragment()
            }
            McPages.CONNECT -> {
                mTitlebar.setTitle("一机多控（联网")
                DoKitMcConnectFragment()
            }
            McPages.CONNECT_HISTORY -> {
                mTitlebar.setTitle("一机多控")
                DoKitMcConnectHistoryFragment()
            }
            McPages.HOST -> {
                mTitlebar.setTitle("一机多控（主机")
                DoKitMcHostFragment()
            }
            McPages.CLIENT -> {
                mTitlebar.setTitle("一机多控（从机")
                DoKitMcClientFragment()
            }

            McPages.CLIENT_HISTORY -> {
                mTitlebar.setTitle("一机多控（从机历史")
                DoKitMcClientHistoryFragment()
            }

            McPages.MC_CASELIST -> {
                mTitlebar.setTitle("一机多控（用例列表")
                DoKitMcDatasFragment()
            }

            else -> {
                mTitlebar.setTitle("一机多控（原)")
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
