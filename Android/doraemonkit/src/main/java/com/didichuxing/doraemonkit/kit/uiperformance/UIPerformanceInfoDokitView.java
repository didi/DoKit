package com.didichuxing.doraemonkit.kit.uiperformance;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.model.ViewInfo;
import com.didichuxing.doraemonkit.ui.base.AbsDokitView;
import com.didichuxing.doraemonkit.ui.base.DokitViewLayoutParams;
import com.didichuxing.doraemonkit.ui.base.DokitViewManager;
import com.didichuxing.doraemonkit.ui.widget.textview.LabelTextView;

import java.util.List;

/**
 * Created by jintai on 2019/09/26.
 */
public class UIPerformanceInfoDokitView extends AbsDokitView implements UIPerformanceManager.PerformanceDataListener {
    private ImageView mClose;
    private LabelTextView mMaxLevelText;
    private LabelTextView mMaxLevelViewIdText;
    private LabelTextView mTotalTimeText;
    private LabelTextView mMaxTimeText;
    private LabelTextView mMaxTimeViewIdText;


    @Override
    public View onCreateView(Context context, FrameLayout view) {
        return LayoutInflater.from(view.getContext()).inflate(R.layout.dk_float_ui_performance_info, view, false);
    }

    @Override
    public void onViewCreated(FrameLayout view) {
        mClose = findViewById(R.id.close);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DokitViewManager.getInstance().detach(UIPerformanceDisplayDokitView.class.getSimpleName());
                DokitViewManager.getInstance().detach(UIPerformanceInfoDokitView.class.getSimpleName());
                UIPerformanceManager.getInstance().stop();
            }
        });
        mMaxLevelText = findViewById(R.id.max_level);
        mMaxLevelViewIdText = findViewById(R.id.max_level_view_id);
        mTotalTimeText = findViewById(R.id.total_time);
        mMaxTimeText = findViewById(R.id.max_time);
        mMaxTimeViewIdText = findViewById(R.id.max_time_view_id);


    }


    @Override
    public void initDokitViewLayoutParams(DokitViewLayoutParams params) {
        params.y = 60;
        params.height = DokitViewLayoutParams.WRAP_CONTENT;
        params.width = DokitViewLayoutParams.WRAP_CONTENT;
    }

    @Override
    public void onCreate(Context context) {
        UIPerformanceManager.getInstance().addListener(UIPerformanceInfoDokitView.this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UIPerformanceManager.getInstance().removeListener(this);
    }

    @Override
    public void onRefresh(List<ViewInfo> viewInfos) {
        if (viewInfos == null) {
            return;
        }
        int maxLevel = 0;
        float maxTime = 0f;
        float totalTime = 0f;
        ViewInfo maxLevelViewInfo = null;
        ViewInfo maxTimeViewInfo = null;
        for (ViewInfo viewInfo : viewInfos) {
            if (viewInfo.layerNum > maxLevel) {
                maxLevel = viewInfo.layerNum;
                maxLevelViewInfo = viewInfo;
            }
            if (viewInfo.drawTime > maxTime) {
                maxTime = viewInfo.drawTime;
                maxTimeViewInfo = viewInfo;
            }
            totalTime += viewInfo.drawTime;
        }
        mMaxLevelText.setText(String.valueOf(maxLevel));
        if (maxLevelViewInfo != null && !TextUtils.isEmpty(maxLevelViewInfo.id)) {
            mMaxLevelViewIdText.setText(maxLevelViewInfo.id);
        }
        mMaxTimeText.setText(maxTime + "ms");
        if (maxTimeViewInfo != null && !TextUtils.isEmpty(maxTimeViewInfo.id)) {
            mMaxTimeViewIdText.setText(maxTimeViewInfo.id);
        }
        mTotalTimeText.setText(totalTime + "ms");
    }


}