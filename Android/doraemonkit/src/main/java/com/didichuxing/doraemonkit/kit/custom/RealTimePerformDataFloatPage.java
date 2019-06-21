package com.didichuxing.doraemonkit.kit.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.PerformanceInfoConfig;
import com.didichuxing.doraemonkit.kit.common.PerformanceDataManager;
import com.didichuxing.doraemonkit.kit.timecounter.TimeCounterManager;
import com.didichuxing.doraemonkit.ui.base.BaseFloatPage;
import com.didichuxing.doraemonkit.ui.base.TouchProxy;
import com.didichuxing.doraemonkit.util.UIUtils;

/**
 *
 */

public class RealTimePerformDataFloatPage extends BaseFloatPage implements TouchProxy.OnTouchEventListener {
    public static final int UPDATE_DATA_WHAT = 0x123;
    private WindowManager mWindowManager;
    private TouchProxy mTouchProxy = new TouchProxy(this);

    TextView mMemoryTxt;
    TextView mDownNetworkTxt;
    TextView mCpuTxt;
    TextView mUpNetworkTxt;
    TextView mFpsTxt;
    private Handler mHandler;

    @Override
    protected void onCreate(Context context) {
        super.onCreate(context);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                showInfo();

                mHandler.sendEmptyMessageDelayed(UPDATE_DATA_WHAT, 1000);
            }
        };
    }

    @Override
    protected View onCreateView(Context context, ViewGroup view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_perform_data, null);
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
        mMemoryTxt = findViewById(R.id.memory_txt);
        mDownNetworkTxt = findViewById(R.id.down_network_txt);
        mCpuTxt = findViewById(R.id.cpu_txt);
        mUpNetworkTxt = findViewById(R.id.up_network_txt);
        mFpsTxt = findViewById(R.id.fps_txt);

        mHandler.sendEmptyMessage(UPDATE_DATA_WHAT);
    }

    @SuppressLint("DefaultLocale")
    private void showInfo() {
        PerformanceDataManager manager = PerformanceDataManager.getInstance();
        if (PerformanceInfoConfig.isMemoryOpen(getContext())) {
            mMemoryTxt.setVisibility(View.VISIBLE);
            mMemoryTxt.setText(String.format("%s:  %.1fM", getString(R.string.dk_frameinfo_ram), manager.getLastMemoryInfo()));
        }else{
            mMemoryTxt.setVisibility(View.INVISIBLE);
        }

        if (PerformanceInfoConfig.isCPUOpen(getContext())) {
            mCpuTxt.setVisibility(View.VISIBLE);
            mCpuTxt.setText(String.format("%s:  %.1f%%", getString(R.string.dk_frameinfo_cpu), manager.getLastCpuRate()));
        }else{
            mCpuTxt.setVisibility(View.INVISIBLE);
        }

        if (PerformanceInfoConfig.isFPSOpen(getContext())) {
            mFpsTxt.setVisibility(View.VISIBLE);
            mFpsTxt.setText(String.format("%s:  %s", getString(R.string.dk_frameinfo_fps), manager.getLastFrameRate() + ""));
        }else{
            mFpsTxt.setVisibility(View.INVISIBLE);
        }

        if (PerformanceInfoConfig.isTrafficOpen(getContext())) {
            mDownNetworkTxt.setVisibility(View.VISIBLE);
            mUpNetworkTxt.setVisibility(View.VISIBLE);

            mDownNetworkTxt.setText(String.format("%s%s", getString(R.string.dk_frameinfo_downstream), getFlowTxt(manager.getLastDownBytes())));
            mUpNetworkTxt.setText(String.format("%s%s", getString(R.string.dk_frameinfo_upstream), getFlowTxt(manager.getLastUpBytes())));
        }else{
            mDownNetworkTxt.setVisibility(View.INVISIBLE);
            mUpNetworkTxt.setVisibility(View.INVISIBLE);
        }
    }

    public static String getFlowTxt(long flowBytes) {
        String upFlowTxt = flowBytes+"B";
        if(1073741824 < flowBytes) {
            upFlowTxt = flowBytes/1073741824 +"GB";
        }else if(1048576 < flowBytes) {
            upFlowTxt = flowBytes/1048576 +"MB";
        }else if(1024 < flowBytes) {
            upFlowTxt = flowBytes/1024 +"KB";
        }

        return upFlowTxt;
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