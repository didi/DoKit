package com.didichuxing.doraemonkit.ui.widget.webview;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.didichuxing.doraemonkit.BuildConfig;
import com.didichuxing.doraemonkit.R;

/**
 * Created by wanglikun on 2019/4/8
 */
public class MyWebView extends WebView {
    private ProgressBar mProgressBar;
    private MyWebViewClient mMyWebViewClient;

    public MyWebView(Context context) {
        super(context);
        init(context);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private Activity mContainerActivity;

    private void init(Context context) {
        if (!(context instanceof Activity)) {
            throw new RuntimeException("only support Activity context");
        } else {
            this.mContainerActivity = (Activity)context;
            WebSettings webSettings = this.getSettings();
            webSettings.setPluginState(WebSettings.PluginState.ON);
            webSettings.setJavaScriptEnabled(true);
            webSettings.setAllowFileAccess(false);
            webSettings.setLoadsImagesAutomatically(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setBuiltInZoomControls(false);
            webSettings.setDefaultTextEncodingName("UTF-8");
            webSettings.setDomStorageEnabled(true);
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(false);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && BuildConfig.DEBUG) {
                setWebContentsDebuggingEnabled(true);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                webSettings.setMixedContentMode(0);
            }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1 && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                this.removeJavascriptInterface("searchBoxJavaBridge_");
                this.removeJavascriptInterface("accessibilityTraversal");
                this.removeJavascriptInterface("accessibility");
            }
            mMyWebViewClient = new MyWebViewClient();
            this.setWebViewClient(mMyWebViewClient);
            this.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    if (newProgress < 100) {
                        showLoadProgress(newProgress);
                    } else {
                        hideLoadProgress();
                    }
                }
            });

            addProgressView();
        }
    }

    public void addInvokeListener(MyWebViewClient.InvokeListener listener) {
        mMyWebViewClient.addInvokeListener(listener);
    }

    public void removeInvokeListener(MyWebViewClient.InvokeListener listener) {
        mMyWebViewClient.removeInvokeListener(listener);
    }

    private void addProgressView() {
        this.mProgressBar = new ProgressBar(this.getContext(), null, android.R.attr.progressBarStyleHorizontal);
        this.mProgressBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 10, 0, 0));
        Integer progressBarColor = getResources().getColor(R.color.dk_color_55A8FD);

        ClipDrawable d = new ClipDrawable(new ColorDrawable(progressBarColor), Gravity.LEFT, ClipDrawable.HORIZONTAL);
        this.mProgressBar.setProgressDrawable(d);
        this.mProgressBar.setVisibility(GONE);
        this.addView(this.mProgressBar);
    }

    public void showLoadProgress(int progress) {
        if (null != this.mProgressBar) {
            if (this.mProgressBar.getVisibility() == GONE) {
                this.mProgressBar.setVisibility(VISIBLE);
            }

            this.mProgressBar.setProgress(progress);
        }
    }

    public void hideLoadProgress() {
        if (null != this.mProgressBar) {
            this.mProgressBar.setVisibility(GONE);
        }

    }

    @Override
    public void loadUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
        }
        super.loadUrl(url);
    }

    public Activity getActivity() {
        return this.mContainerActivity;
    }
}