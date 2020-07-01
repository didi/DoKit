package com.didichuxing.doraemonkit.kit.layoutborder

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.FrameLayout
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.AbsDokitView
import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams
import com.didichuxing.doraemonkit.kit.core.UniversalActivity
import com.didichuxing.doraemonkit.util.LifecycleListenerUtil
import com.didichuxing.doraemonkit.util.LifecycleListenerWrapper
import com.didichuxing.doraemonkit.util.LogHelper.i
import com.didichuxing.doraemonkit.util.UIUtils.dp2px
import com.didichuxing.doraemonkit.util.UIUtils.getDokitAppContentView
import com.didichuxing.doraemonkit.util.UIUtils.heightPixels

/**
 * 控制显示布局层级的自定义view
 *
 * @author Donald Yan
 * @date 2020/6/19
 */
class LayoutLevelDokitView : AbsDokitView() {


    companion object {
        private const val TAG = "LayoutLevelDokitView"
    }

    private var enable = false

    private var mScalpelFrameLayout: ScalpelFrameLayout? = null

    private val mLifecycleListenerWrapper = object : LifecycleListenerWrapper() {
        override fun onActivityResumed(activity: Activity?) {
            super.onActivityResumed(activity)
            resolveActivity(activity)
        }
    }

    override fun onCreate(context: Context) {
        resolveActivity(ActivityUtils.getTopActivity())
        LifecycleListenerUtil.registerListener(mLifecycleListenerWrapper)
    }

    private fun resolveActivity(activity: Activity?) {
        if (activity == null || activity is UniversalActivity) {
            return
        }
        val window = activity.window ?: return
        val appContentView: ViewGroup? = if (isNormalMode) {
            getDokitAppContentView(activity) as ViewGroup?
        } else {
            window.decorView as ViewGroup
        }

        if (appContentView == null) {
            ToastUtils.showShort("当前根布局不支持该功能")
            return
        }

        if (appContentView.toString().contains("SwipeBackLayout")) {
            i(TAG, "普通模式下布局层级功能暂不支持以SwipeBackLayout为根布局,请改用系统模式")
            ToastUtils.showLong("普通模式下布局层级功能暂不支持以SwipeBackLayout为根布局")
            return
        }

        //将所有控件放入到ScalpelFrameLayout中
        mScalpelFrameLayout = ScalpelFrameLayout(appContentView.context)
        mScalpelFrameLayout!!.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        mScalpelFrameLayout!!.isLayerInteractionEnabled = enable
        while (appContentView.childCount != 0) {
            val child = appContentView.getChildAt(0)
            if (child is ScalpelFrameLayout) {
                mScalpelFrameLayout = child
                return
            }
            appContentView.removeView(child)
            mScalpelFrameLayout!!.addView(child)
        }
        appContentView.addView(mScalpelFrameLayout)
    }

    override fun onCreateView(context: Context, rootView: FrameLayout): View {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_layout_level, rootView, false)
    }

    override fun onViewCreated(rootView: FrameLayout) {
        findViewById<View>(R.id.iv_layout_close).setOnClickListener {
            LayoutManager.instance.close()
        }
        findViewById<CheckBox>(R.id.cb_layout_level_switch).setOnCheckedChangeListener { _, isChecked ->
            enable = isChecked
            mScalpelFrameLayout?.isLayerInteractionEnabled = isChecked
        }
    }

    override fun initDokitViewLayoutParams(params: DokitViewLayoutParams?) {
        params!!.gravity = Gravity.CENTER_HORIZONTAL
        params.x = 0
        params.y = heightPixels - dp2px(125F)
        //解决页面跳转是view的宽度会发生变化
        params.width = screenShortSideLength
        params.height = DokitViewLayoutParams.WRAP_CONTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        mScalpelFrameLayout?.isLayerInteractionEnabled = false
        mScalpelFrameLayout = null
        LifecycleListenerUtil.unRegisterListener(mLifecycleListenerWrapper)
    }
}