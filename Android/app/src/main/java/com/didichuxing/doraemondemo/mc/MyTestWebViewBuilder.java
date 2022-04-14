package com.didichuxing.doraemondemo.mc;

import android.text.TextUtils;

import com.didichuxing.doraemonkit.util.LogHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * didi Create on 2022/3/22 .
 * <p>
 * Copyright (c) 2022/3/22 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/3/22 7:14 下午
 * @Description 用一句话说明文件功能
 */

public class MyTestWebViewBuilder {

    public final String TAG = "WebViewBuilder";

    private MyProxyWebView mKDWebView;
    private String initUrl;
    private final Map<String, String> additionalHttpHeaders = new HashMap<String, String>();


    public MyTestWebViewBuilder(MyProxyWebView mKDWebView, String initUrl) {
        this.mKDWebView = mKDWebView;
        this.initUrl = initUrl;
    }

    public void load(String url, Map<String, String> headers) {
        mKDWebView.getWebView().loadUrl(url, headers);
    }


    /**
     * 返回WebView
     */
    public MyProxyWebView create() {
        if (!TextUtils.isEmpty(initUrl)) {
            LogHelper.d(TAG, "load initurl:" + initUrl);
            mKDWebView.getWebView().loadUrl(initUrl, additionalHttpHeaders);
        }
        return mKDWebView;
    }

    /**
     * 返回WebView
     */
    public MyProxyWebView create2(String a, String b, String c, String d, String e, String f) {
        if (!TextUtils.isEmpty(initUrl)) {
            LogHelper.d(TAG, "load initurl:" + initUrl);
            String dx = initUrl;
            mKDWebView.getWebView().loadUrl(f, additionalHttpHeaders);
        }
        return mKDWebView;
    }

}
