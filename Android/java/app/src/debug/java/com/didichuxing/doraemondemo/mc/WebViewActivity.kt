package com.didichuxing.doraemondemo.mc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.didichuxing.doraemondemo.R
import kotlinx.android.synthetic.main.activity_webview.*

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
        webview.loadUrl("https://m.baidu.com")
    }
}