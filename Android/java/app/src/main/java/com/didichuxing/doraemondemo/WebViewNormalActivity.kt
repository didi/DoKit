package com.didichuxing.doraemondemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * Created by wanglikun on 2018/11/13.
 */
class WebViewNormalActivity : AppCompatActivity() {
    //private var mWebView: WebView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_normal_webview)
        val mWebView = findViewById<WebView>(R.id.web_view)
        mWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                mWebView.loadUrl(url)
                return true
            }
        }
        val intent = intent
        if (intent == null) {
            finish()
            return
        }
        val url = intent.getStringExtra(KEY_URL)
        if (TextUtils.isEmpty(url)) {
            finish()
            return
        }
        mWebView.loadUrl(url)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        const val KEY_URL = "key_url"
    }
}