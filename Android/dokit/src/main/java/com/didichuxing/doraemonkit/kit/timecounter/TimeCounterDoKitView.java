package com.didichuxing.doraemonkit.kit.timecounter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.timecounter.bean.CounterInfo;
import com.didichuxing.doraemonkit.kit.core.AbsDoKitView;
import com.didichuxing.doraemonkit.kit.core.DoKitViewLayoutParams;
import com.didichuxing.doraemonkit.util.UIUtils;

/**
 * Created by jintai on 2019/09/26.
 */
public class TimeCounterDoKitView extends AbsDoKitView {
    private TextView tvTitle;
    private TextView tvTotal;
    private TextView tvPause;
    private TextView tvLaunch;
    private TextView tvRender;
    private TextView tvOther;
    private ImageView mClose;

    @Override
    public void onCreate(Context context) {
    }

    @Override
    public View onCreateView(Context context, FrameLayout view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_time_counter, null);
    }

//    @Override
//    public void onNormalLayoutParamsCreated(FrameLayout.LayoutParams params) {
//        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        params.leftMargin = UIUtils.dp2px(getContext(), 30);
//        params.topMargin = UIUtils.dp2px(getContext(), 30);
//        super.onNormalLayoutParamsCreated(params);
//    }
//
//    @Override
//    public void onSystemLayoutParamsCreated(WindowManager.LayoutParams params) {
//        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        params.x = UIUtils.dp2px(getContext(), 30);
//        params.y = UIUtils.dp2px(getContext(), 30);
//        super.onSystemLayoutParamsCreated(params);
//    }

    @Override
    public void initDokitViewLayoutParams(DoKitViewLayoutParams params) {
        params.width = DoKitViewLayoutParams.WRAP_CONTENT;
        params.height = DoKitViewLayoutParams.WRAP_CONTENT;
        params.x = UIUtils.dp2px(30);
        params.y = UIUtils.dp2px(30);
    }

    @Override
    public void onViewCreated(FrameLayout view) {
        initView();
    }

    private void initView() {
        tvTitle = findViewById(R.id.title);
        tvTotal = findViewById(R.id.total_cost);
        tvPause = findViewById(R.id.pause_cost);
        tvLaunch = findViewById(R.id.launch_cost);
        tvRender = findViewById(R.id.render_cost);
        tvOther = findViewById(R.id.other_cost);

        CounterInfo counterInfo = TimeCounterManager.get().getAppSetupInfo();
        showInfo(counterInfo);

        mClose = findViewById(R.id.close);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeCounterManager.get().stop();
            }
        });
    }

    public void showInfo(CounterInfo info) {
        tvTitle.setText(info.title);
        setTotalCost(info.totalCost);

        if (info.type == CounterInfo.TYPE_ACTIVITY) {
            tvPause.setVisibility(View.VISIBLE);
            tvLaunch.setVisibility(View.VISIBLE);
            tvRender.setVisibility(View.VISIBLE);
            tvOther.setVisibility(View.VISIBLE);

            tvPause.setText("Pause Cost: " + info.pauseCost + "ms");
            tvLaunch.setText("Launch Cost: " + info.launchCost + "ms");
            tvRender.setText("Render Cost: " + info.renderCost + "ms");
            tvOther.setText("Other Cost: " + info.otherCost + "ms");
        } else {
            tvPause.setVisibility(View.GONE);
            tvLaunch.setVisibility(View.GONE);
            tvRender.setVisibility(View.GONE);
            tvOther.setVisibility(View.GONE);
        }
    }

    private void setTotalCost(long cost) {
        tvTotal.setText("Total Cost: " + cost + "ms");
        if (cost <= CounterInfo.ACTIVITY_COST_FAST) {
            tvTotal.setTextColor(getContext().getResources().getColor(R.color.dk_color_48BB31));
        } else if (cost <= CounterInfo.ACTIVITY_COST_SLOW) {
            tvTotal.setTextColor(getContext().getResources().getColor(R.color.dk_color_FAD337));
        } else {
            tvTotal.setTextColor(getContext().getResources().getColor(R.color.dk_color_FF0006));
        }
    }

    private void showDetail(CounterInfo info) {
        if (info.type == CounterInfo.TYPE_APP) {
            info.show = false;
        }
        if (info.show) {
            tvPause.setVisibility(View.VISIBLE);
            tvLaunch.setVisibility(View.VISIBLE);
            tvRender.setVisibility(View.VISIBLE);
            tvOther.setVisibility(View.VISIBLE);


        } else {
            tvPause.setVisibility(View.GONE);
            tvLaunch.setVisibility(View.GONE);
            tvRender.setVisibility(View.GONE);
            tvOther.setVisibility(View.GONE);
        }
    }


    @Override
    public void onEnterForeground() {
        super.onEnterForeground();
    }

    @Override
    public void onEnterBackground() {
        super.onEnterBackground();
        TimeCounterManager.get().onEnterBackground();
    }
}
