package com.didichuxing.doraemonkit.kit.webdoor

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.BundleKey
import com.didichuxing.doraemonkit.kit.core.BaseFragment

/**
 * Created by guofeng007 on 2020/6/8
 */
class WebDoorDefaultFragment : BaseFragment() {
    private var mUrl: String? = null
    private lateinit var mWebView: WebView
    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_web_door_default
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mUrl = arguments!!.getString(BundleKey.KEY_URL)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mWebView = findViewById(R.id.web_view)
        mWebView.loadUrl(mUrl)
    }

    override fun onBackPressed(): Boolean {
        return if (mWebView.canGoBack()) {
            mWebView.goBack()
            true
        } else {
            super.onBackPressed()
        }
    }
}