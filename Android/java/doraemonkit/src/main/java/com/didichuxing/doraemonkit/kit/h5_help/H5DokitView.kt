package com.didichuxing.doraemonkit.kit.h5_help

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.children
import androidx.webkit.WebViewCompat
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.AbsDokitView
import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams
import com.didichuxing.doraemonkit.kit.core.DokitViewManager

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/8/25-11:08
 * 描    述：
 * 修订历史：
 * ================================================
 */
class H5DokitView : AbsDokitView() {
    lateinit var mTvLink: TextView
    override fun onCreate(context: Context?) {
    }

    override fun onCreateView(context: Context?, rootView: FrameLayout?): View {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_h5_info, rootView, false)
    }

    override fun onViewCreated(rootView: FrameLayout?) {
        rootView?.let {
            val close = it.findViewById<ImageView>(R.id.iv_close)
            close.setOnClickListener {
                DokitViewManager.getInstance().detach(this)
            }
            mTvLink = it.findViewById(R.id.tv_link)
        }
    }

    override fun initDokitViewLayoutParams(params: DokitViewLayoutParams?) {
        params?.let {
            it.width = ConvertUtils.dp2px(300.0f)
            it.height = DokitViewLayoutParams.WRAP_CONTENT
            it.gravity = Gravity.TOP or Gravity.LEFT
            it.x = 200
            it.y = 200
        }
    }

    @SuppressLint("JavascriptInterface", "AddJavascriptInterface")
    override fun onResume() {
        super.onResume()
        val webView = performTraverseView()
        if (webView == null) {
            mTvLink.text = "当前页面不存在WebView"
        } else {
            mTvLink.text = webView.url

        }
    }

    /**
     * 更新url
     */
    public fun updateUrl(url: String?) {
        mTvLink.text = url
    }


    private fun performTraverseView(): WebView? {
        val decorView = activity.window.decorView as ViewGroup
        decorView.children.forEach {
            if (it is WebView) {
                return it
            } else if (it is ViewGroup) {
                return traversView(it)

            }
        }
        return null
    }

    private fun traversView(viewGroup: ViewGroup): WebView? {
        viewGroup.children.forEach {
            if (it is WebView) {
                return it
            } else if (it is ViewGroup) {
                return traversView(it)
            }
        }
        return null
    }


}