package com.didichuxing.doraemonkit.kit.colorpick

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import com.blankj.utilcode.util.ActivityUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.AbsDokitView
import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams
import com.didichuxing.doraemonkit.kit.core.DokitViewManager
import com.didichuxing.doraemonkit.kit.core.TranslucentActivity
import com.didichuxing.doraemonkit.util.ColorUtil
import com.didichuxing.doraemonkit.util.UIUtils.dp2px
import com.didichuxing.doraemonkit.util.UIUtils.heightPixels

/**
 * 显示色值相关信息的类。
 * 主要显示颜色和色值
 * @author Donald Yan
 * @date 2020/6/15
 */
class ColorPickerInfoDokitView: AbsDokitView() {

    private lateinit var mColorHex: TextView
    private lateinit var mColor: ImageView

    override fun onCreate(context: Context) {
    }

    override fun onCreateView(context: Context, rootView: FrameLayout): View {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_color_picker_info, null)
    }

    override fun onViewCreated(rootView: FrameLayout) {
        mColorHex = findViewById(R.id.tv_info_hex)
        mColor = findViewById(R.id.iv_info_color)
        findViewById<ImageView>(R.id.iv_info_close).setOnClickListener {
            DokitViewManager.instance.detach(ColorPickerDokitView::class.java.simpleName)
            DokitViewManager.instance.detach(ColorPickerInfoDokitView::class.java.simpleName)
            //取色器kit是依赖在当前透明的Activity上的 所以关闭控件时需要finish
            if (ActivityUtils.getTopActivity() != null && ActivityUtils.getTopActivity() is TranslucentActivity) {
                ActivityUtils.getTopActivity().finish()
            }
        }
    }

    override fun initDokitViewLayoutParams(params: DokitViewLayoutParams?) {
        params!!.width = screenShortSideLength
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        params.x = 0
        params.y = heightPixels - dp2px(95F)
    }

    fun showInfo(@ColorInt color: Int, x: Int, y: Int) {
        mColor.setImageDrawable(ColorDrawable(color))
        mColorHex.text = "${ColorUtil.parseColorInt(color)}   ${x + ColorPickConstants.PIX_INTERVAL}, ${y + ColorPickConstants.PIX_INTERVAL}"
    }
}