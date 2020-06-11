package com.didichuxing.doraemonkit.kit.colorpick

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.AbsDokitView
import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams

/**
 *
 * @author Donald Yan
 * @date 2020/6/5
 */
class ColorPickerDokitView : AbsDokitView(){

    override fun onCreate(context: Context?) {

    }

    override fun onCreateView(context: Context?, rootView: FrameLayout?): View {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_color_picker, null)
    }

    override fun onViewCreated(rootView: FrameLayout?) {
    }

    override fun initDokitViewLayoutParams(params: DokitViewLayoutParams?) {
        Log.e("ColorPickerDokitView", "initDokitViewLayoutParams: $params")
        params!!.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        params.height = DokitViewLayoutParams.WRAP_CONTENT
        params.width = DokitViewLayoutParams.WRAP_CONTENT
    }

    override fun onEnterBackground() {
        //进入后台时，不调用父方法进行隐藏
    }

    override fun onEnterForeground() {
        //进入前台时，不调用父方法进行显示
    }

    override fun restrictBorderline(): Boolean {
        return false
    }
}