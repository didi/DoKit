package com.didichuxing.doraemonkit.weex.log;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.didichuxing.doraemonkit.kit.loginfo.LogInfoDoKitView;
import com.didichuxing.doraemonkit.kit.loginfo.LogInfoManager;
import com.didichuxing.doraemonkit.kit.loginfo.LogLine;
import com.didichuxing.doraemonkit.weex.R;
import com.didichuxing.doraemonkit.widget.titlebar.LogTitleBar;

import java.util.ArrayList;
import java.util.List;

/**
 * @author haojianglong
 * @date 2019-06-25
 */
public class WeexLogInfoDoKitView extends LogInfoDoKitView {

    private final String WEEX_TAG = "weex";

    @Override
    public View onCreateView(Context context, FrameLayout view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_weex_float_log_info, null);
    }


    @Override
    public void initView() {
        super.initView();

        LogTitleBar logTitleBar = findViewById(R.id.dokit_title_bar);
        logTitleBar.setListener(new LogTitleBar.OnTitleBarClickListener() {
            @Override
            public void onRightClick() {
                //关闭日志服务
                LogInfoManager.getInstance().stop();
                //清空回调
                LogInfoManager.getInstance().removeListener();
                detach();
            }

            @Override
            public void onLeftClick() {
                minimize();
            }
        });


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
