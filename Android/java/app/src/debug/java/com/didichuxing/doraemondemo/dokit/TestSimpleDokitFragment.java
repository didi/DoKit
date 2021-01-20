package com.didichuxing.doraemondemo.dokit;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;

import com.didichuxing.doraemondemo.R;
import com.didichuxing.doraemonkit.kit.core.SimpleDokitFragment;
import com.didichuxing.doraemonkit.kit.core.SimpleDokitStarter;
import com.didichuxing.doraemonkit.kit.core.ViewSetupHelper;

/**
 * @Author: changzuozhen
 * @Date: 2020-12-22
 * 切换全屏与否只需要调整继承关系即可
 * @see TestSimpleDokitFragment
 * @see TestSimpleDokitFloatView
 * <p>
 * 悬浮窗，支持折叠
 * @see com.didichuxing.doraemonkit.kit.core.SimpleDokitView
 * 启动工具函数
 * @see com.didichuxing.doraemonkit.kit.core.SimpleDokitStarter.startFloating
 * <p>
 * 全屏页面
 * @see com.didichuxing.doraemonkit.kit.core.SimpleDokitFragment
 * 启动工具函数
 * @see com.didichuxing.doraemonkit.kit.core.SimpleDokitStarter.startFullScreen(java.lang.Class<? extends com.didichuxing.doraemonkit.kit.core.SimpleDokitFragment>, android.content.Context, android.os.Bundle)
 */
public class TestSimpleDokitFragment extends SimpleDokitFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.layout_demo_custom;
    }

    @Override
    protected void onViewCreated(@NonNull View rootView) {
        super.onViewCreated(rootView);

        ViewSetupHelper.setupButton(rootView, R.id.test1, "TestSimpleDokitFragment", v -> {
            Bundle bundle = new Bundle();
            bundle.putString("test", "test");
            SimpleDokitStarter.startFullScreen(TestSimpleDokitFragment.class, getContext());
        });

        // 隐藏
        ViewSetupHelper.setupButton(rootView, R.id.test2, "", v -> {
        });

        ViewSetupHelper.setupToggleButton(rootView, R.id.tb_test1, "TB", false, (CompoundButton buttonView, boolean isChecked) -> {
        });

        // 隐藏
        ViewSetupHelper.setupToggleButton(rootView, R.id.tb_test2, "", false, (CompoundButton buttonView, boolean isChecked) -> {
        });
    }
}
