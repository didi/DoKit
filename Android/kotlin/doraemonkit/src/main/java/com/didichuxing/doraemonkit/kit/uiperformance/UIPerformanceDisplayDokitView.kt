package com.didichuxing.doraemonkit.kit.uiperformance

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.AbsDokitView
import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams
import com.didichuxing.doraemonkit.kit.uiperformance.UIPerformanceManager.Companion.instance
import com.didichuxing.doraemonkit.kit.uiperformance.UIPerformanceManager.PerformanceDataListener
import com.didichuxing.doraemonkit.kit.viewcheck.LayoutBorderView
import com.didichuxing.doraemonkit.model.ViewInfo

/**
 *
 * Desc:ui界面层级信息相关
 * <p>
 * Date: 2020-06-15
 * Company:
 * Updater:
 * Update Time:
 * Update Comments:
 *
 * Author: pengyushan
 */
class UIPerformanceDisplayDokitView : AbsDokitView(), PerformanceDataListener {
    private var mLayoutBorderView: LayoutBorderView? = null
    override fun onCreateView(context: Context?, view: FrameLayout?): View {
        return LayoutInflater.from(view!!.context).inflate(R.layout.dk_float_ui_performance_display, view, false)
    }

    override fun onViewCreated(view: FrameLayout?) {
        mLayoutBorderView = findViewById(R.id.rect_view)
        //设置不响应触摸事件
        setDokitViewNotResponseTouchEvent(rootView)
    }

    override fun initDokitViewLayoutParams(params: DokitViewLayoutParams?) {
        params!!.flags = DokitViewLayoutParams.FLAG_NOT_FOCUSABLE_AND_NOT_TOUCHABLE
        params.width = DokitViewLayoutParams.MATCH_PARENT
        params.height = DokitViewLayoutParams.MATCH_PARENT
    }

    override fun onCreate(context: Context?) {
        instance.addListener(this@UIPerformanceDisplayDokitView)
    }

    override fun onDestroy() {
        super.onDestroy()
        instance.removeListener(this)
    }

    override fun onRefresh(infos: List<ViewInfo>?) {
        mLayoutBorderView!!.showViewLayoutBorder(infos)
    }

    override fun canDrag(): Boolean {
        return false
    }
}