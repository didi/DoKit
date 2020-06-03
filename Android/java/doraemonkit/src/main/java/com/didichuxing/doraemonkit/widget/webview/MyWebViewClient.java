package com.didichuxing.doraemonkit.widget.webview;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanglikun on 2019/4/15
 */
public class MyWebViewClient extends WebViewClient {
    private List<InvokeListener> mListeners = new ArrayList<>();

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith("doraemon://invokeNative")) {
            handleInvokeFromJs(url);
            return true;
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    private void handleInvokeFromJs(String url) {
        for (InvokeListener listener : mListeners) {
            listener.onNativeInvoke(url);
        }
    }

    public void addInvokeListener(InvokeListener listener) {
        mListeners.add(listener);
    }

    public void removeInvokeListener(InvokeListener listener) {
        mListeners.remove(listener);
    }

    public interface InvokeListener {
        void onNativeInvoke(String url);
    }
}