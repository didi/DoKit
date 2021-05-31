package com.didichuxing.doraemondemo.mc

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.didichuxing.doraemondemo.R

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/29-21:58
 * 描    述：
 * 修订历史：
 * ================================================
 */
class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        findViewById<WebView>(R.id.webview).loadUrl("https://m.baidu.com")
    }
}