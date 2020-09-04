package com.didichuxing.doraemonkit.kit.h5_help

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.*
import com.blankj.utilcode.util.ConvertUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.DokitConstant
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
                DokitViewManager.getInstance().detach(this)
            }
            mTvLink = it.findViewById(R.id.tv_link)
            mJsCheckBox = it.findViewById(R.id.js_switch)
            mJsCheckBox.isChecked = DokitConstant.H5_JS_INJECT
            mJsCheckBox.setOnCheckedChangeListener { _, isChecked ->
                DokitConstant.H5_JS_INJECT = isChecked
            }

            mVConsoleCheckBox = it.findViewById(R.id.vConsole_switch)
            mVConsoleCheckBox.isChecked = DokitConstant.H5_VCONSOLE_INJECT
            mVConsoleCheckBox.setOnCheckedChangeListener { _, isChecked ->
                DokitConstant.H5_VCONSOLE_INJECT = isChecked
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
                invalidate()
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
            mMoreWrap.visibility = View.GONE
        } else {
            mTvLink.text = webView.url
            mMoreWrap.visibility = View.VISIBLE
        }
        mJsCheckBox.isChecked = DokitConstant.H5_JS_INJECT
        invalidate()
    }

    /**
     * 更新url
     */
    fun updateUrl(url: String?) {
        mTvLink.text = url
        invalidate()
    }


    private fun performTraverseView(): WebView? {
        val decorView = activity.window.decorView as ViewGroup
        for (index in 0 until decorView.childCount) {
            val child = decorView.getChildAt(index)
            if (child is WebView) {
                return child
            } else if (child is ViewGroup) {
                return traversView(child)
            }
        }

        return null
    }

    private fun traversView(viewGroup: ViewGroup): WebView? {
        for (index in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(index)
            if (child is WebView) {
                return child
            } else if (child is ViewGroup) {
                return traversView(child)
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


    override fun invalidate() {
        normalLayoutParams?.let {
            it.width = ConvertUtils.dp2px(300.0f)
            if (isOpen) {
                it.height = ConvertUtils.dp2px(650.0f)
            } else {
                it.height = DokitViewLayoutParams.WRAP_CONTENT
            }

        }
        super.invalidate()
    }

    /**
     * 不限制屏幕边界
     */
    override fun restrictBorderline(): Boolean {
        return false
    }


}