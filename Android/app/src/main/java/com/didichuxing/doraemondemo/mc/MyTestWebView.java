package com.didichuxing.doraemondemo.mc;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import java.util.Map;

/**
 * didi Create on 2022/3/22 .
 * <p>
 * Copyright (c) 2022/3/22 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/3/22 3:56 下午
 * @Description 用一句话说明文件功能
 */

public class MyTestWebView extends WebView {

    public MyTestWebView(Context context) {
        super(context);
    }

    public MyTestWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTestWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyTestWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void loadUrl(String url) {
        super.loadUrl(url);
    }

    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        super.loadUrl(url, additionalHttpHeaders);
    }
}
