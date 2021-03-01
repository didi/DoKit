package com.didichuxing.doraemonkit.kit.mc.all.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.didichuxing.doraemonkit.kit.core.BaseActivity
import com.didichuxing.doraemonkit.kit.mc.all.McConstant
import com.didichuxing.doraemonkit.kit.mc.all.WSMode
import com.didichuxing.doraemonkit.mc.R
import kotlinx.android.synthetic.main.dk_activity_mc.*

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

    companion object {
        const val FRAGMENT_HOST = 100
        const val FRAGMENT_CLIENT = 101
        const val FRAGMENT_SELECT = 102
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dk_activity_mc)
        title_bar.setListener {
            finish()
        }
        when (McConstant.WS_MODE) {
            WSMode.UNKNOW -> {
                changeFragment(FRAGMENT_SELECT)
            }
            WSMode.HOST -> {
                changeFragment(FRAGMENT_HOST)
            }
            WSMode.CLIENT -> {
                changeFragment(FRAGMENT_CLIENT)
            }
        }
    }


    fun changeFragment(fragmentType: Int) {
        val fragment: Fragment = when (fragmentType) {
            FRAGMENT_HOST -> {
                DoKitMcHostFragment()
            }
            FRAGMENT_CLIENT -> {
                DoKitMcClientFragment()
            }
            FRAGMENT_SELECT -> {
                DoKitMcSelectFragment()
            }
            else -> {
                DoKitMcSelectFragment()
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragment)
            .commitNowAllowingStateLoss()
    }

}