package com.didichuxing.doraemondemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by wanglikun on 2018/11/13.
 */

public class WebViewActivity extends AppCompatActivity {
    public static final String KEY_URL = "key_url";
    private WebView mWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        mWebView = findViewById(R.id.web_view);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mWebView.loadUrl(url);
                return true;
            }
        });
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        String url = intent.getStringExtra(KEY_URL);
        if (TextUtils.isEmpty(url)) {
            finish();
            return;
        }
        mWebView.loadUrl(url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}