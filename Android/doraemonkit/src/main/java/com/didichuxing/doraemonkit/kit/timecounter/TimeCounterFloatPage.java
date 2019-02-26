package com.didichuxing.doraemonkit.kit.timecounter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.timecounter.bean.CounterInfo;
import com.didichuxing.doraemonkit.ui.base.BaseFloatPage;
import com.didichuxing.doraemonkit.ui.base.TouchProxy;
import com.didichuxing.doraemonkit.util.UIUtils;

/**
 */

public class TimeCounterFloatPage extends BaseFloatPage implements TouchProxy.OnTouchEventListener {
    private WindowManager mWindowManager;
    private TextView tvTitle;
    private TextView tvTotal;
    private TextView tvPause;
    private TextView tvLaunch;
    private TextView tvRender;
    private TextView tvOther;
    private ImageView mClose;
    private TouchProxy mTouchProxy = new TouchProxy(this);

    @Override
    protected void onCreate(Context context) {
        super.onCreate(context);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    protected View onCreateView(Context context, ViewGroup view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_time_counter, null);
    }

    @Override
    protected void onLayoutParamsCreated(WindowManager.LayoutParams params) {
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.x = UIUtils.dp2px(getContext(), 30);
        params.y = UIUtils.dp2px(getContext(), 30);
    }

    @Override
    protected void onViewCreated(View view) {
        super.onViewCreated(view);
        initView();
    }

    private void initView() {
        getRootView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mTouchProxy.onTouchEvent(v, event);
            }
        });
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
    public void onMove(int x, int y, int dx, int dy) {
        getLayoutParams().x += dx;
        getLayoutParams().y += dy;
        mWindowManager.updateViewLayout(getRootView(), getLayoutParams());
    }

    @Override
    public void onUp(int x, int y) {

    }

    @Override
    public void onDown(int x, int y) {

    }

    @Override
    public void onEnterForeground() {
        super.onEnterForeground();
        getRootView().setVisibility(View.VISIBLE);
    }

    @Override
    public void onEnterBackground() {
        super.onEnterBackground();
        getRootView().setVisibility(View.GONE);
        TimeCounterManager.get().onEnterBackground();
    }
}