package com.didichuxing.doraemonkit.kit.mc.all.ui

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.IdRes
import com.didichuxing.doraemonkit.mc.R
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar
import com.didichuxing.doraemonkit.zxing.activity.CaptureActivity
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
class DoKitMcScanActivity : CaptureActivity() {

    override fun setContentView(@IdRes layoutResID: Int) {
        super.setContentView(layoutResID)
        initTitleBar()
    }

    private fun initTitleBar() {
        val homeTitleBar = HomeTitleBar(this)
        homeTitleBar.setBackgroundColor(resources.getColor(R.color.foreground_wtf))
        homeTitleBar.setTitle(resources.getString(R.string.dk_kit_multi_control))
        homeTitleBar.setIcon(R.mipmap.dk_close_icon)
        homeTitleBar.setListener { finish() }
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            resources.getDimension(R.dimen.dk_home_title_height).toInt()
        )
        (findViewById<View>(android.R.id.content) as FrameLayout).addView(homeTitleBar, params)
    }
}