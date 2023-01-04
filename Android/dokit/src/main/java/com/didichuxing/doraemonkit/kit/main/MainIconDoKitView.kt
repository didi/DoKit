package com.didichuxing.doraemonkit.kit.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.config.FloatIconConfig
import com.didichuxing.doraemonkit.datapick.DataPickManager
import com.didichuxing.doraemonkit.datapick.DataPickUtils
import com.didichuxing.doraemonkit.kit.core.AbsDoKitView
import com.didichuxing.doraemonkit.kit.core.DoKitViewLayoutParams

/**
 * 悬浮按钮
 * Created by jintai on 2019/09/26.
 */
class MainIconDoKitView : AbsDoKitView() {

    init {
        viewProps.edgePinned = true
    }

    override fun onCreate(context: Context) {}

    override fun onViewCreated(view: FrameLayout) {
        //设置id便于查找
        doKitView?.id = R.id.float_icon_id
        //设置icon 点击事件
        doKitView?.setOnClickListener { //统计入口
            val pageId = DataPickUtils.getCurrentPage()
            DataPickUtils.setDoKitHomeClickPage(pageId)
            DataPickManager.getInstance().addData("dokit_sdk_home_ck_entry", pageId)
            DoKit.showToolPanel()
        }

//        DataPickManager.getInstance().addData("dokit_sdk_home_show", DataPickUtils.getCurrentPage())
    }

    override fun onCreateView(context: Context, view: FrameLayout): View {
        return LayoutInflater.from(context).inflate(R.layout.dk_main_launch_icon, view, false)
    }

    override fun initDokitViewLayoutParams(params: DoKitViewLayoutParams) {
        params.x = FloatIconConfig.getLastPosX()
        params.y = FloatIconConfig.getLastPosY()
        params.width = DoKitViewLayoutParams.WRAP_CONTENT
        params.height = DoKitViewLayoutParams.WRAP_CONTENT
    }

    override fun onResume() {
        super.onResume()
        if (isNormalMode) {
            immInvalidate()
        }
    }
}
