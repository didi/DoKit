package com.didichuxing.doraemonkit.widget.webview

import android.app.Activity
import android.content.Context
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.ProgressBar
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.widget.webview.MyWebViewClient.InvokeListener

/**
 * Created by guofeng007 on 2020/6/8
 */
class MyWebView : WebView {
    private var mProgressBar: ProgressBar? = null
    private var mMyWebViewClient: MyWebViewClient? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    var activity: Activity? = null

    private fun init(context: Context) {
        if (context !is Activity) {
            throw RuntimeException("only support Activity context")
        } else {
            activity = context
            val webSettings = this.settings
            webSettings.pluginState = WebSettings.PluginState.ON
            webSettings.javaScriptEnabled = true
            webSettings.allowFileAccess = false
            webSettings.loadsImagesAutomatically = true
            webSettings.useWideViewPort = true
            webSettings.builtInZoomControls = false
            webSettings.defaultTextEncodingName = "UTF-8"
            webSettings.domStorageEnabled = true
            webSettings.cacheMode = WebSettings.LOAD_DEFAULT
            webSettings.javaScriptCanOpenWindowsAutomatically = false
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                setWebContentsDebuggingEnabled(true)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                webSettings.mixedContentMode = 0
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1 && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                removeJavascriptInterface("searchBoxJavaBridge_")
                removeJavascriptInterface("accessibilityTraversal")
                removeJavascriptInterface("accessibility")
            }
            mMyWebViewClient = MyWebViewClient()
            this.webViewClient = mMyWebViewClient
            this.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    if (newProgress < 100) {
                        showLoadProgress(newProgress)
                    } else {
                        hideLoadProgress()
                    }
                }
            }
            addProgressView()
        }
    }

    fun addInvokeListener(listener: InvokeListener) {
        mMyWebViewClient!!.addInvokeListener(listener)
    }

    fun removeInvokeListener(listener: InvokeListener) {
        mMyWebViewClient!!.removeInvokeListener(listener)
    }

    private fun addProgressView() {
        mProgressBar = ProgressBar(this.context, null, android.R.attr.progressBarStyleHorizontal)
        mProgressBar!!.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 10, 0, 0)
        val progressBarColor = resources.getColor(R.color.dk_color_55A8FD)
        val d = ClipDrawable(ColorDrawable(progressBarColor), Gravity.LEFT, ClipDrawable.HORIZONTAL)
        mProgressBar!!.progressDrawable = d
        mProgressBar!!.visibility = View.GONE
        this.addView(mProgressBar)
    }

    fun showLoadProgress(progress: Int) {
        if (null != mProgressBar) {
            if (mProgressBar!!.visibility == View.GONE) {
                mProgressBar!!.visibility = View.VISIBLE
            }
            mProgressBar!!.progress = progress
        }
    }

    fun hideLoadProgress() {
        if (null != mProgressBar) {
            mProgressBar!!.visibility = View.GONE
        }
    }

    override fun loadUrl(url: String) {
        var url = url
        if (!TextUtils.isEmpty(url)) {
            if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("javascript:")) {
                url = "http://$url"
            }
        }
        super.loadUrl(url)
    }

}