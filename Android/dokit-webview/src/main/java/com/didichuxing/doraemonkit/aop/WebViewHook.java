package com.didichuxing.doraemonkit.aop;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.webkit.WebViewCompat;

import com.didichuxing.doraemonkit.kit.core.DoKitManager;
import com.didichuxing.doraemonkit.kit.h5_help.DoKitJSI;
import com.didichuxing.doraemonkit.kit.h5_help.DoKitWebViewClient;
import com.didichuxing.doraemonkit.kit.h5_help.DoKitX5WebViewClient;
import com.didichuxing.doraemonkit.kit.h5_help.X5WebViewUtil;
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


    public static String getSafeUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (DoKitManager.H5_DOKIT_MC_INJECT && ((String) url).startsWith("https")) {
                return url.replaceFirst("https", "http");
            } else {
                return url;
            }
        }
        return "";
    }

    /**
     * webview inject java object
     */
    public static void inject(Object webView) {
        //LogHelper.i(TAG, "====inject====");
        if (webView != null) {
            //先判断是否引入了X5WebView
            if (X5WebViewUtil.INSTANCE.hasImpX5WebViewLib()) {
                if (webView instanceof WebView) {
                    injectNormal((WebView) webView);
                } else if (webView instanceof com.tencent.smtt.sdk.WebView) {
                    injectX5((com.tencent.smtt.sdk.WebView) webView);
                }
            } else {
                if (webView instanceof WebView) {
                    injectNormal((WebView) webView);
                }
            }
        }
    }


    @SuppressLint({"AddJavascriptInterface", "RequiresFeature", "SetJavaScriptEnabled"})
    private static void injectNormal(WebView webView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            webView.setWebContentsDebuggingEnabled(true);
            if (!(WebViewCompat.getWebViewClient(webView) instanceof DoKitWebViewClient)) {
                WebSettings settings = webView.getSettings();
                settings.setJavaScriptEnabled(true);
                settings.setDatabaseEnabled(true);
                settings.setDomStorageEnabled(true);
                settings.setAllowUniversalAccessFromFileURLs(true);
                webView.addJavascriptInterface(new DoKitJSI(), "dokitJsi");
                webView.setWebViewClient(new DoKitWebViewClient(WebViewCompat.getWebViewClient(webView), settings.getUserAgentString()));
            }
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    private static void injectX5(com.tencent.smtt.sdk.WebView webView) {
        LogHelper.i(TAG, "====injectX5====");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!(webView.getWebViewClient() instanceof DoKitX5WebViewClient)) {
                com.tencent.smtt.sdk.WebSettings settings = webView.getSettings();
                settings.setJavaScriptEnabled(true);
                settings.setAllowUniversalAccessFromFileURLs(true);
                webView.addJavascriptInterface(new DoKitJSI(), "dokitJsi");
                webView.setWebViewClient(new DoKitX5WebViewClient(webView.getWebViewClient(), settings.getUserAgentString()));
            }
        }
    }
}

