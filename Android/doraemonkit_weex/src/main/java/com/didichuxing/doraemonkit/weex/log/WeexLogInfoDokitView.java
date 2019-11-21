package com.didichuxing.doraemonkit.weex.log;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.didichuxing.doraemonkit.kit.logInfo.LogInfoDokitView;
import com.didichuxing.doraemonkit.kit.logInfo.LogLine;
import com.didichuxing.doraemonkit.ui.widget.titlebar.HomeTitleBar;
import com.didichuxing.doraemonkit.weex.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author haojianglong
 * @date 2019-06-25
 */
public class WeexLogInfoDokitView extends LogInfoDokitView {

    private final String WEEX_TAG = "weex";


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
                detach();
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
        //过滤出weex日志
        List<LogLine> newLines = new ArrayList<>();
        for (LogLine logLine : logLines) {
            if (logLine.getTag().contains(WEEX_TAG)) {
                newLines.add(logLine);
            }
        }
        super.onLogCatch(newLines);
    }


}
