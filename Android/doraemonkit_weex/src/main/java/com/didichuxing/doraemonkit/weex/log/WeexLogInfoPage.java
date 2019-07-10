package com.didichuxing.doraemonkit.weex.log;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.didichuxing.doraemonkit.kit.logInfo.LogInfoFloatPage;
import com.didichuxing.doraemonkit.kit.logInfo.LogLine;
import com.didichuxing.doraemonkit.ui.widget.titlebar.HomeTitleBar;
import com.didichuxing.doraemonkit.weex.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author haojianglong
 * @date 2019-06-25
 */
public class WeexLogInfoPage extends LogInfoFloatPage {

    private final String WEEX_TAG = "weex";

    private static PageCloseCallback mCallback;

    public static void setCallback(PageCloseCallback callback) {
        mCallback = callback;
    }

    @Override
    public void initView() {
        super.initView();
        findViewById(R.id.title_bar).setVisibility(View.GONE);
        LinearLayout rootView = findViewById(R.id.log_page);
        HomeTitleBar homeTitleBar = new HomeTitleBar(getContext());
        homeTitleBar.setBackgroundColor(getResources().getColor(R.color.foreground_wtf));
        homeTitleBar.setTitle(getResources().getString(R.string.dk_console_log_title));
        homeTitleBar.setIcon(R.drawable.dk_close_icon);
        homeTitleBar.setListener(new HomeTitleBar.OnTitleBarClickListener() {
            @Override
            public void onRightClick() {
                finish();
            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) getResources().getDimension(R.dimen.dk_home_title_height));
        rootView.addView(homeTitleBar, 0, params);
    }

    @Override
    public void onLogCatch(List<LogLine> logLines) {
        if (logLines == null || logLines.size() <= 0) {
            return;
        }
        List<LogLine> newLines = new ArrayList<>();
        for (LogLine logLine : logLines) {
            if (WEEX_TAG.equals(logLine.getTag())) {
                newLines.add(logLine);
            }
        }
        super.onLogCatch(newLines);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCallback != null) {
            mCallback.onPageDestroy();
            mCallback = null;
        }
    }

    interface PageCloseCallback {
        /**
         * 页面关闭
         */
        void onPageDestroy();
    }

}
