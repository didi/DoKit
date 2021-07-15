package com.didichuxing.doraemonkit.kit.mc.all.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.kit.core.BaseActivity
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
    lateinit var mTitlebar: HomeTitleBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dk_activity_mc)

        mTitlebar = findViewById(R.id.title_bar)
        mTitlebar.setListener {
            finish()
        }

        changeFragment(WSMode.UNKNOW)
    }


    fun changeFragment(wsMode: WSMode) {
        val fragment: Fragment = when (wsMode) {
            WSMode.RECORDING,
            WSMode.UNKNOW -> {
                mTitlebar.setTitle("一机多控（主页）")
                DoKitMcMainFragment()
            }
            WSMode.HOST -> {
                mTitlebar.setTitle("一机多控（主机）")
                DoKitMcHostFragment()
            }
            WSMode.CLIENT -> {
                mTitlebar.setTitle("一机多控（从机）")
                DoKitMcClientFragment()
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

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragment)
            .commitNowAllowingStateLoss()
    }

}