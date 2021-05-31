package com.didichuxing.doraemonkit.kit.webdoor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.View;
import android.webkit.WebView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.kit.core.BaseFragment;
import com.didichuxing.doraemonkit.kit.webview.WebViewManager;

/**
 * Created by wanglikun on 2019/4/4
 */
public class WebDoorDefaultFragment extends BaseFragment {

    //private String mUrl;

    private WebView mWebView;

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_web_door_default;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //mUrl = getArguments() == null ? null : getArguments().getString(BundleKey.KEY_URL);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWebView = findViewById(R.id.webview);
        if (WebViewManager.INSTANCE.getUrl() != null && !WebViewManager.INSTANCE.getUrl().isEmpty()) {
            mWebView.loadUrl(WebViewManager.INSTANCE.getUrl());
        }

    }

    @Override
    public boolean onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        } else {
            return super.onBackPressed();
        }
    }
}