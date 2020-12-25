package com.didichuxing.doraemonkit.kit.core;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar;

/**
 * @Author: changzuozhen
 * @Date: 2020-12-22
 * <p>
 * 全屏页面
 * @see com.didichuxing.doraemonkit.kit.core.SimpleDokitFragment
 * 启动工具函数
 * @see com.didichuxing.doraemonkit.kit.core.SimpleDokitStarter.startFullScreen(java.lang.Class<? extends com.didichuxing.doraemonkit.kit.core.SimpleDokitFragment>, android.content.Context, android.os.Bundle)
 */
public abstract class SimpleDokitFragment extends BaseFragment {
    private static final String TAG = "SimpleFragment";

    public Bundle getBundle() {
        if (getActivity() == null || getActivity().getIntent() == null || getActivity().getIntent().getExtras() == null) {
            return null;
        }
        return getActivity().getIntent().getExtras();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        inflater.inflate(getLayoutId(), (FrameLayout) rootView.findViewById(R.id.contentContainer), true);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        onViewCreated(view);
    }

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_simple_dokit_page;
    }

    protected void onViewCreated(View view) {
    }

    abstract protected @LayoutRes
    int getLayoutId();

    private void initView() {
        HomeTitleBar homeTitleBar = findViewById(R.id.title_bar);
        homeTitleBar.setTitle(this.getClass().getSimpleName());
        homeTitleBar.setListener(new HomeTitleBar.OnTitleBarClickListener() {
            @Override
            public void onRightClick() {
                getActivity().finish();
            }
        });
    }

}
