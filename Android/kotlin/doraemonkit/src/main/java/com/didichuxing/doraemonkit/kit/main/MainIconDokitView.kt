package com.didichuxing.doraemonkit.kit.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.didichuxing.doraemonkit.DoraemonKit.showToolPanel
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.config.FloatIconConfig
import com.didichuxing.doraemonkit.kit.core.AbsDokitView
import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams

/**
 * 悬浮按钮
 * Created by jintai on 2019/09/26.
 */
class MainIconDokitView : AbsDokitView() {
    override fun onCreate(context: Context?) {}
    override fun onViewCreated(view: FrameLayout?) {
        //设置id便于查找
        rootView?.id = R.id.float_icon_id
        //设置icon 点击事件
        rootView?.setOnClickListener { //统计入口
            //DataPickManager.getInstance().addData("dokit_sdk_home_ck_entry"); //TODO("功能待实现")
            showToolPanel()
        }
    }

    override fun onCreateView(context: Context?, view: FrameLayout?): View {
        return LayoutInflater.from(context).inflate(R.layout.dk_main_launch_icon, view, false)
    }

    override fun initDokitViewLayoutParams(params: DokitViewLayoutParams?) {
        params?.let {
            params.x = FloatIconConfig.getLastPosX()
            params.y = FloatIconConfig.getLastPosY()
            params.width = FLOAT_SIZE
            params.height = FLOAT_SIZE
        }

    }

    override fun onResume() {
        super.onResume()
        if (isNormalMode) {
            val params = normalLayoutParams
            params.width = FLOAT_SIZE
            params.height = FLOAT_SIZE
            invalidate()
        }
    }

    companion object {
        var FLOAT_SIZE = 174
    }
}