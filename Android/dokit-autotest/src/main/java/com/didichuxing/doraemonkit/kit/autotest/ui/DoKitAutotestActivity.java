package com.didichuxing.doraemonkit.kit.autotest.ui;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.didichuxing.doraemonkit.autotest.R;
import com.didichuxing.doraemonkit.kit.core.BaseFragment;
import com.didichuxing.doraemonkit.kit.core.NewBaseActivity;
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar;

/**
 * didi Create on 2022/4/6 .
 * <p>
 * Copyright (c) 2022/4/6 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/6 5:24 下午
 * @Description 用一句话说明文件功能
 */

public class DoKitAutotestActivity extends NewBaseActivity {

    private HomeTitleBar homeTitleBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dk_activity_autotest);
        homeTitleBar = findViewById(R.id.title_bar);
        homeTitleBar.setListener(new HomeTitleBar.OnTitleBarClickListener() {
            @Override
            public void onRightClick() {
                onBackPressed();
            }
        });

        String pageName = getIntent().getStringExtra("PAGE_NAME");
        if (TextUtils.isEmpty(pageName)) {
            newHomeFragment();
        } else {
            AutotestPage page = AutotestPage.valueOf(pageName);
            changeFragment(page);
        }
    }


    public void newHomeFragment() {
        changeFragment(AutotestPage.HOME);
    }

    public void changeFragment(AutotestPage page) {
        changeFragment(page, false);
    }

    public void pushFragment(AutotestPage page) {
        changeFragment(page, true);
    }

    public void changeFragment(AutotestPage page, boolean push) {
        BaseFragment fragment;
        switch (page) {
            case CONNECT:
                fragment = new DoKitAutotestConnectFragment();
                break;
            case RECORD:
            case CASE_LIST:

            case HOME:
            default:
                fragment = new DoKitAutotestFragment();
        }

        if (push) {
            showContent(R.id.fragment_container_view, fragment);
        } else {
            replaceContent(R.id.fragment_container_view, fragment);
        }

    }

}
