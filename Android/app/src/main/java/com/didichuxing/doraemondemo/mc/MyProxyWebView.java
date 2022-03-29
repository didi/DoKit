package com.didichuxing.doraemondemo.mc;

import android.webkit.WebView;

/**
 * didi Create on 2022/3/22 .
 * <p>
 * Copyright (c) 2022/3/22 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/3/22 7:15 下午
 * @Description 用一句话说明文件功能
 */

public class MyProxyWebView {


    private WebView webView;

    public MyProxyWebView(WebView webView) {
        this.webView = webView;
    }

    public WebView getWebView() {
        return webView;
    }
}
