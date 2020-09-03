package com.didichuxing.doraemonkit.aop;

import android.annotation.SuppressLint;
import android.os.Build;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.webkit.WebViewCompat;

import com.didichuxing.doraemonkit.constant.DokitConstant;
import com.didichuxing.doraemonkit.kit.h5_help.DokitJSI;
import com.didichuxing.doraemonkit.kit.h5_help.DokitWebViewClient;
import com.didichuxing.doraemonkit.util.LogHelper;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/8/31-11:39
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class WebViewHook {
    private static final String TAG = "WebViewHook";

    /**
     * webview inject java object
     */
    @SuppressLint({"AddJavascriptInterface", "RequiresFeature", "SetJavaScriptEnabled"})
    public static void inject(WebView webView) {
        LogHelper.i(TAG, "====inject====");
        if (webView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!(WebViewCompat.getWebViewClient(webView) instanceof DokitWebViewClient)) {
                    WebSettings settings = webView.getSettings();
                    settings.setJavaScriptEnabled(true);
                    settings.setAllowUniversalAccessFromFileURLs(true);
                    webView.addJavascriptInterface(new DokitJSI(), "dokitJsi");
                    webView.setWebViewClient(new DokitWebViewClient(WebViewCompat.getWebViewClient(webView)));
                }
            }
        }

    }
}

