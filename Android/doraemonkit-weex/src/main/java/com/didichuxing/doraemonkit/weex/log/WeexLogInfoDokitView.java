package com.didichuxing.doraemonkit.weex.log;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.didichuxing.doraemonkit.kit.logInfo.LogInfoDokitView;
import com.didichuxing.doraemonkit.kit.logInfo.LogLine;
import com.didichuxing.doraemonkit.ui.widget.titlebar.HomeTitleBar;
import com.didichuxing.doraemonkit.ui.widget.titlebar.TitleBar;
import com.didichuxing.doraemonkit.weex.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author haojianglong
 * @date 2019-06-25
 */
public class WeexLogInfoDokitView extends LogInfoDokitView {

    private final String WEEX_TAG = "weex";
    FrameLayout mHomeTitleBar;

    @Override
    public View onCreateView(Context context, FrameLayout view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_weex_float_log_info, null);
    }


    @Override
    public void initView() {
        super.initView();

        mHomeTitleBar = findViewById(R.id.dokit_title_bar);
        if (mHomeTitleBar instanceof HomeTitleBar) {
            ((HomeTitleBar) mHomeTitleBar).setListener(new HomeTitleBar.OnTitleBarClickListener() {
                @Override
                public void onRightClick() {
                    detach();
                }
            });
        }


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
