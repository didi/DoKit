package com.didichuxing.doraemonkit.kit.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.PerformanceSpInfoConfig;
import com.didichuxing.doraemonkit.kit.common.PerformanceDataManager;
import com.didichuxing.doraemonkit.kit.timecounter.TimeCounterManager;
import com.didichuxing.doraemonkit.ui.base.AbsDokitView;
import com.didichuxing.doraemonkit.ui.base.DokitViewLayoutParams;
import com.didichuxing.doraemonkit.ui.base.DokitViewManager;
import com.didichuxing.doraemonkit.util.UIUtils;

/**
 * Created by jintai on 2019/09/26.
 */
public class RealTimePerformDataDokitView extends AbsDokitView {
    public static final int UPDATE_DATA_WHAT = 0x123;

    TextView mMemoryTxt;
    TextView mDownNetworkTxt;
    TextView mCpuTxt;
    TextView mUpNetworkTxt;
    ImageView mIvClose;
    TextView mFpsTxt;
    private Handler mHandler;

    @Override
    public void onCreate(Context context) {

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                showInfo();

                mHandler.sendEmptyMessageDelayed(UPDATE_DATA_WHAT, 1000);
            }
        };
        PerformanceDataManager.getInstance().init(context);
    }

    @Override
    public View onCreateView(Context context, FrameLayout view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_perform_data, null);
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
    public void initDokitViewLayoutParams(DokitViewLayoutParams params) {
        params.width = DokitViewLayoutParams.WRAP_CONTENT;
        params.height = DokitViewLayoutParams.WRAP_CONTENT;
        params.x = UIUtils.dp2px(getContext(), 30);
        params.y = UIUtils.dp2px(getContext(), 30);
    }

    @Override
    public void onViewCreated(FrameLayout view) {
        initView();
    }

    private void initView() {
        mMemoryTxt = findViewById(R.id.memory_txt);
        mDownNetworkTxt = findViewById(R.id.down_network_txt);
        mCpuTxt = findViewById(R.id.cpu_txt);
        mUpNetworkTxt = findViewById(R.id.up_network_txt);
        mFpsTxt = findViewById(R.id.fps_txt);
        mIvClose = findViewById(R.id.iv_close);
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DokitViewManager.getInstance().detach(RealTimePerformDataDokitView.class.getSimpleName());
            }
        });

        mHandler.sendEmptyMessage(UPDATE_DATA_WHAT);
    }

    @SuppressLint("DefaultLocale")
    private void showInfo() {
        PerformanceDataManager manager = PerformanceDataManager.getInstance();
        if (PerformanceSpInfoConfig.isMemoryOpen(getContext())) {
            mMemoryTxt.setVisibility(View.VISIBLE);
            mMemoryTxt.setText(String.format("%s:  %.1fM", getString(R.string.dk_frameinfo_ram), manager.getLastMemoryInfo()));
        } else {
            mMemoryTxt.setVisibility(View.INVISIBLE);
        }

        if (PerformanceSpInfoConfig.isCPUOpen(getContext())) {
            mCpuTxt.setVisibility(View.VISIBLE);
            mCpuTxt.setText(String.format("%s:  %.1f%%", getString(R.string.dk_frameinfo_cpu), manager.getLastCpuRate()));
        } else {
            mCpuTxt.setVisibility(View.INVISIBLE);
        }

        if (PerformanceSpInfoConfig.isFPSOpen(getContext())) {
            mFpsTxt.setVisibility(View.VISIBLE);
            mFpsTxt.setText(String.format("%s:  %s", getString(R.string.dk_frameinfo_fps), manager.getLastFrameRate() + ""));
        } else {
            mFpsTxt.setVisibility(View.INVISIBLE);
        }

        if (PerformanceSpInfoConfig.isTrafficOpen(getContext())) {
            mDownNetworkTxt.setVisibility(View.VISIBLE);
            mUpNetworkTxt.setVisibility(View.VISIBLE);

            mDownNetworkTxt.setText(String.format("%s%s", getString(R.string.dk_frameinfo_downstream), getFlowTxt(manager.getLastDownBytes())));
            mUpNetworkTxt.setText(String.format("%s%s", getString(R.string.dk_frameinfo_upstream), getFlowTxt(manager.getLastUpBytes())));
        } else {
            mDownNetworkTxt.setVisibility(View.INVISIBLE);
            mUpNetworkTxt.setVisibility(View.INVISIBLE);
        }
    }

    public static String getFlowTxt(long flowBytes) {
        String upFlowTxt = flowBytes + "B";
        if (1073741824 < flowBytes) {
            upFlowTxt = flowBytes / 1073741824 + "GB";
        } else if (1048576 < flowBytes) {
            upFlowTxt = flowBytes / 1048576 + "MB";
        } else if (1024 < flowBytes) {
            upFlowTxt = flowBytes / 1024 + "KB";
        }

        return upFlowTxt;
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