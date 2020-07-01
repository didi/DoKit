package com.didichuxing.doraemonkit.kit.viewcheck

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.blankj.utilcode.util.ActivityUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.AbsDokitView
import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams
import com.didichuxing.doraemonkit.kit.core.DokitViewManager
import com.didichuxing.doraemonkit.kit.viewcheck.ViewCheckDokitView.OnViewSelectListener
import com.didichuxing.doraemonkit.model.ViewInfo

/**
 * @author: xuchun
 * @time: 2020/6/4 - 14:47
 * @desc: 控件检查绘制View
 */
class ViewCheckDrawDokitView : AbsDokitView(), OnViewSelectListener {
    private var mLayoutBorderView: LayoutBorderView? = null
    override fun onCreate(context: Context) {}
    override fun onDestroy() {
        super.onDestroy()
        val page = DokitViewManager.instance.getDokitView(ActivityUtils.getTopActivity(), ViewCheckDokitView::class.java.simpleName) as ViewCheckDokitView?
        page?.removeViewSelectListener(this)
    }

    override fun onCreateView(context: Context, rootView: FrameLayout): View {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_view_check_draw, null)
    }

    override fun initDokitViewLayoutParams(params: DokitViewLayoutParams?) {
        params?.let {
            it.flags = DokitViewLayoutParams.FLAG_NOT_FOCUSABLE_AND_NOT_TOUCHABLE
            it.width = DokitViewLayoutParams.MATCH_PARENT
            it.height = DokitViewLayoutParams.MATCH_PARENT
        }

    }

    override fun onViewCreated(rootView: FrameLayout) {
        mLayoutBorderView = findViewById(R.id.rect_view)
        setDokitViewNotResponseTouchEvent(this.rootView)
        postDelayed(200, Runnable {
            val dokitView = DokitViewManager.instance.getDokitView(ActivityUtils.getTopActivity(), ViewCheckDokitView::class.java.simpleName) as ViewCheckDokitView?
            dokitView?.setViewSelectListener(this@ViewCheckDrawDokitView)
        })
    }

    override fun onViewSelected(current: View?, checkViewList: List<View>) {
        if (current == null) {
            mLayoutBorderView!!.showViewLayoutBorder(null as ViewInfo?)
        } else {
            mLayoutBorderView!!.showViewLayoutBorder(ViewInfo(current))
        }
    }

    /**
     * 解决ViewCheckDrawDokitView的margin被改变的bug
     */
    override fun onResume() {
        super.onResume()
        normalLayoutParams.setMargins(0, 0, 0, 0)
        normalLayoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT
        normalLayoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT
        invalidate()
    }

    override fun canDrag(): Boolean {
        return false
    }
}