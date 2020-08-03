package com.didichuxing.doraemonkit.kit.methodtrace;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.view.View;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.kit.core.BaseFragment;
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar;
import com.didichuxing.doraemonkit.widget.webview.MyWebView;

/**
 * @author jintai
 * @desc: 函数耗时功能介绍
 */

public class MethodCostFragment extends BaseFragment {
    MyWebView mWebView;

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_method_cost;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }


    private void initView() {
        HomeTitleBar title = findViewById(R.id.title_bar);
        title.setListener(new HomeTitleBar.OnTitleBarClickListener() {
            @Override
            public void onRightClick() {
                getActivity().finish();
            }
        });
        mWebView = findViewById(R.id.webview);
        mWebView.loadUrl(NetworkManager.APP_DOCUMENT_URL);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}