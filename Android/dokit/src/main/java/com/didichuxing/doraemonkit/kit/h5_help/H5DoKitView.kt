package com.didichuxing.doraemonkit.kit.h5_help

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.util.ConvertUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.kit.core.AbsDoKitView
import com.didichuxing.doraemonkit.kit.core.DoKitViewLayoutParams

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/8/25-11:08
 * 描    述：
 * 修订历史：
 * ================================================
 */
class H5DoKitView : AbsDoKitView() {
    companion object {
        const val STORAGE_TYPE_LOCAL = 100
        const val STORAGE_TYPE_Session = 101
    }

    /**
     * url链接
     */
    private lateinit var mTvLink: TextView

    /**
     * js hook switch
     */
    private lateinit var mJsCheckBox: CheckBox
    private lateinit var mVConsoleCheckBox: CheckBox
    private lateinit var mRvLocal: RecyclerView
    private lateinit var mRvSession: RecyclerView
    private lateinit var mRvWrap: LinearLayout
    private lateinit var mMoreWrap: RelativeLayout
    private lateinit var mHolder: TextView
    private lateinit var mLocalAdapter: LocalStorageAdapter
    private lateinit var mSessionAdapter: LocalStorageAdapter
    private lateinit var mNavLocal: TextView
    private lateinit var mNavSession: TextView
    private lateinit var mBtnReload: Button

    /**
     * 更多信息是否处于展开状态
     */
    var isOpen: Boolean = false


    override fun onCreate(context: Context?) {
    }

    override fun onCreateView(context: Context?, rootView: FrameLayout?): View {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_h5_info, rootView, false)
    }

    override fun onViewCreated(rootView: FrameLayout?) {
        rootView?.let {
            val close = it.findViewById<ImageView>(R.id.iv_close)
            close.setOnClickListener {
                DoKit.removeFloating(this)
            }
            mTvLink = it.findViewById(R.id.tv_link)
            mJsCheckBox = it.findViewById(R.id.js_switch)
            mBtnReload = it.findViewById(R.id.btn_reload)
            mBtnReload.setOnClickListener {
                mWebView?.let { webView ->
                    if (X5WebViewUtil.hasImpX5WebViewLib()) {
                        when (webView) {
                            is WebView -> {
                                webView.reload()
                            }
                            is com.tencent.smtt.sdk.WebView -> {
                                webView.reload()
                            }
                        }
                    } else {
                        when (webView) {
                            is WebView -> {
                                webView.reload()
                            }
                        }
                    }
                }
            }
            mJsCheckBox.isChecked = DoKitManager.H5_JS_INJECT
            mJsCheckBox.setOnCheckedChangeListener { _, isChecked ->
                DoKitManager.H5_JS_INJECT = isChecked
            }

            mVConsoleCheckBox = it.findViewById(R.id.vConsole_switch)
            mVConsoleCheckBox.isChecked = DoKitManager.H5_VCONSOLE_INJECT
            mVConsoleCheckBox.setOnCheckedChangeListener { _, isChecked ->
                DoKitManager.H5_VCONSOLE_INJECT = isChecked
            }

            mNavLocal = it.findViewById(R.id.tv_nav_local)
            mNavLocal.setTextColor(ContextCompat.getColor(activity, R.color.dk_color_55A8FD))
            mNavLocal.setOnClickListener {
                mRvLocal.visibility = View.VISIBLE
                mRvSession.visibility = View.GONE
                mNavSession.setTextColor(ContextCompat.getColor(activity, R.color.dk_color_333333))
                mNavLocal.setTextColor(ContextCompat.getColor(activity, R.color.dk_color_55A8FD))
            }
            mNavSession = it.findViewById(R.id.tv_nav_session)
            mNavSession.setTextColor(ContextCompat.getColor(activity, R.color.dk_color_333333))
            mNavSession.setOnClickListener {
                mRvLocal.visibility = View.GONE
                mRvSession.visibility = View.VISIBLE
                mNavSession.setTextColor(ContextCompat.getColor(activity, R.color.dk_color_55A8FD))
                mNavLocal.setTextColor(ContextCompat.getColor(activity, R.color.dk_color_333333))
            }

            mRvWrap = it.findViewById(R.id.ll_rv_wrap)
            mMoreWrap = it.findViewById(R.id.rl_more_wrap)
            mHolder = it.findViewById(R.id.tv_holder)
            mHolder.text = "更多"
            mHolder.setOnClickListener {
                if (mRvWrap.visibility == View.VISIBLE) {
                    mRvWrap.visibility = View.GONE
                    mHolder.text = "更多"
                    isOpen = false
                } else {
                    mRvWrap.visibility = View.VISIBLE
                    mHolder.text = "收起"
                    isOpen = true
                }
                immInvalidate()
            }


            //绑定localStorage数据

            mRvLocal = it.findViewById(R.id.rv_localStorage)
            mRvLocal.visibility = View.VISIBLE
            mRvLocal.layoutManager = LinearLayoutManager(activity)
            mLocalAdapter = LocalStorageAdapter(JsHookDataManager.jsLocalStorage)
            mRvLocal.adapter = mLocalAdapter
            mLocalAdapter.setEmptyView(R.layout.dk_layout_localstorage_empty)


            //绑定sessionStorage数据
            mRvSession = it.findViewById(R.id.rv_sessionStorage)
            mRvSession.visibility = View.GONE
            mRvSession.layoutManager = LinearLayoutManager(activity)
            mSessionAdapter = LocalStorageAdapter(JsHookDataManager.jsSessionStorage)
            mRvSession.adapter = mSessionAdapter
            mSessionAdapter.setEmptyView(R.layout.dk_layout_sessionstorage_empty)
        }
    }


    override fun initDokitViewLayoutParams(params: DoKitViewLayoutParams?) {
        params?.let {
            it.width = ConvertUtils.dp2px(300.0f)
            it.height = DoKitViewLayoutParams.WRAP_CONTENT
            it.gravity = Gravity.TOP or Gravity.LEFT
            it.x = 200
            it.y = 200
        }
    }

    var mWebView: Any? = null

    @SuppressLint("JavascriptInterface", "AddJavascriptInterface")
    override fun onResume() {
        super.onResume()
        mWebView = performTraverseView()
        if (mWebView == null) {
            mTvLink.text = "当前页面不存在WebView"
            mMoreWrap.visibility = View.GONE
            mBtnReload.visibility = View.GONE
        } else {
            if (X5WebViewUtil.hasImpX5WebViewLib()) {
                when (mWebView) {
                    is WebView -> {
                        mWebView as WebView
                        mTvLink.text = (mWebView as WebView).url
                    }

                    is com.tencent.smtt.sdk.WebView -> {
                        mTvLink.text = (mWebView as com.tencent.smtt.sdk.WebView).url
                    }
                }
            } else {
                when (mWebView) {
                    is WebView -> {
                        mWebView as WebView
                        mTvLink.text = (mWebView as WebView).url
                    }
                }
            }
            mMoreWrap.visibility = View.VISIBLE
            mBtnReload.visibility = View.VISIBLE
        }
        mJsCheckBox.isChecked = DoKitManager.H5_JS_INJECT
        mVConsoleCheckBox.isChecked = DoKitManager.H5_VCONSOLE_INJECT
        immInvalidate()
    }

    /**
     * 更新url
     */
    fun updateUrl(url: String?) {
        mTvLink.text = url
        immInvalidate()
    }


    private fun performTraverseView(): Any? {
        val decorView = activity.window.decorView as ViewGroup
        decorView.children.forEach {
            if (X5WebViewUtil.hasImpX5WebViewLib()) {
                when (it) {
                    is WebView -> return it
                    is com.tencent.smtt.sdk.WebView -> return it
                    is ViewGroup -> return traversView(it)
                }
            } else {
                when (it) {
                    is WebView -> return it
                    is ViewGroup -> return traversView(it)
                }
            }


        }
        return null
    }

    private fun traversView(viewGroup: ViewGroup): Any? {
        viewGroup.children.forEach {
            if (X5WebViewUtil.hasImpX5WebViewLib()) {
                when (it) {
                    is WebView -> return it
                    is com.tencent.smtt.sdk.WebView -> return it
                    is ViewGroup -> return traversView(it)
                }
            } else {
                when (it) {
                    is WebView -> return it
                    is ViewGroup -> return traversView(it)
                }
            }

        }
        return null
    }

    /**
     * 更新adapter的数据
     * @param type 100:localStorage   101:sessionStorage
     */
    fun updateAdapter(type: Int) {
        activity.runOnUiThread {
            if (type == STORAGE_TYPE_LOCAL) {
                mLocalAdapter.notifyDataSetChanged()
            } else if (type == STORAGE_TYPE_Session) {
                mSessionAdapter.notifyDataSetChanged()
            }
        }
    }


    override fun immInvalidate() {
        normalLayoutParams?.let {
            it.width = ConvertUtils.dp2px(300.0f)
            if (isOpen) {
                it.height = ConvertUtils.dp2px(650.0f)
            } else {
                it.height = DoKitViewLayoutParams.WRAP_CONTENT
            }

        }
        super.immInvalidate()
    }

    /**
     * 不限制屏幕边界
     */
    override fun restrictBorderline(): Boolean {
        return false
    }


}
