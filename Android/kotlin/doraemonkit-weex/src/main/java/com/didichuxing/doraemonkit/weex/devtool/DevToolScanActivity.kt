package com.didichuxing.doraemonkit.weex.devtool

import android.view.ViewGroup
import android.widget.FrameLayout
import com.didichuxing.doraemonkit.weex.R
import com.didichuxing.doraemonkit.weex.util.containerView
import com.didichuxing.doraemonkit.weex.util.getColorCompat
import com.didichuxing.doraemonkit.weex.util.getDimensionPixel
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar
import com.king.zxing.CaptureActivity

/**
 * Created by alvince on 2020/7/1
 *
 * @author alvince.zy@gmail.com
 */
class DevToolScanActivity : CaptureActivity() {

    override fun initUI() {
        // init title
        HomeTitleBar(this).apply {
            setBackgroundColor(getColorCompat(R.color.foreground_wtf))
            setTitle(R.string.dk_dev_tool_title)
            setIcon(R.mipmap.dk_close_icon)
            setListener(object : HomeTitleBar.OnTitleBarClickListener {
                override fun onRightClick() {
                    finish()
                }
            })
        }.also { titleBar ->
            containerView()?.addView(
                titleBar,
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    getDimensionPixel(R.dimen.dk_home_title_height)
                )
            )
        }
        super.initUI()
    }

}