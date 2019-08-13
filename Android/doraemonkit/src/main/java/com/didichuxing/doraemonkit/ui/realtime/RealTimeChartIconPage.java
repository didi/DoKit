package com.didichuxing.doraemonkit.ui.realtime;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.common.PerformanceDataManager;
import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.ui.base.BaseFloatPage;
import com.didichuxing.doraemonkit.ui.base.FloatPageManager;
import com.didichuxing.doraemonkit.ui.base.PageIntent;
import com.didichuxing.doraemonkit.util.UIUtils;

import static com.didichuxing.doraemonkit.ui.realtime.datasource.DataSourceFactory.TYPE_CPU;
import static com.didichuxing.doraemonkit.ui.realtime.datasource.DataSourceFactory.TYPE_FRAME;
import static com.didichuxing.doraemonkit.ui.realtime.datasource.DataSourceFactory.TYPE_MEMORY;
import static com.didichuxing.doraemonkit.ui.realtime.datasource.DataSourceFactory.TYPE_NETWORK;


/**
 * @desc: 由于实时折线图浮窗不能接受touch事件，将关闭按钮封装到另外一层
 */
public class RealTimeChartIconPage extends BaseFloatPage implements View.OnClickListener {
    public static final String TAG = "RealTimeChartIconPage";
    private OnFloatPageChangeListener mListener;

    @Override
    protected View onCreateView(Context context, ViewGroup view) {
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setImageResource(R.drawable.dk_close_white);
        imageView.setOnClickListener(this);
        return imageView;
    }

    @Override
    protected void onCreate(Context context) {
        super.onCreate(context);
        PerformanceDataManager.getInstance().init(context);
    }

    @Override
    protected void onViewCreated(View view) {
        super.onViewCreated(view);
    }


    @Override
    protected void onLayoutParamsCreated(WindowManager.LayoutParams params) {
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.RIGHT | Gravity.TOP;
        params.width = UIUtils.dp2px(getContext(), 40);
        params.height = UIUtils.dp2px(getContext(), 40);
    }

    public void setListener(OnFloatPageChangeListener listener) {
        mListener = listener;
    }

    protected static void openChartIconPage(int type, OnFloatPageChangeListener listener) {
        FloatPageManager.getInstance().remove(RealTimeChartIconPage.TAG);
        PageIntent pageIntent = new PageIntent(RealTimeChartIconPage.class);
        pageIntent.mode = PageIntent.MODE_SINGLE_INSTANCE;
        pageIntent.tag = RealTimeChartIconPage.TAG;
        Bundle bundle = new Bundle();
        bundle.putInt(RealTimeChartPage.KEY_TYPE, type);
        pageIntent.bundle = bundle;
        FloatPageManager.getInstance().add(pageIntent);

        RealTimeChartIconPage page = (RealTimeChartIconPage) FloatPageManager.getInstance().getFloatPage(RealTimeChartIconPage.TAG);
        if (page != null) {
            page.setListener(listener);
        }
    }

    protected static void closeChartIconPage() {
        FloatPageManager.getInstance().remove(RealTimeChartPage.TAG);
        FloatPageManager.getInstance().remove(RealTimeChartIconPage.TAG);
    }

    @Override
    public void onClick(View v) {
        closeChartIconPage();
        notifyPageClose();
        if (mListener != null) {
            mListener.onFloatPageClose(TAG);
        }
    }

    /**
     * 关闭浮窗时，不同入口需要执行不同逻辑
     */
    private void notifyPageClose() {
        int type = getBundle().getInt(RealTimeChartPage.KEY_TYPE);
        switch (type) {
            case TYPE_NETWORK:
                NetworkManager.get().stopMonitor();
                break;
            case TYPE_CPU:
                PerformanceDataManager.getInstance().stopMonitorCPUInfo();
                break;
            case TYPE_MEMORY:
                PerformanceDataManager.getInstance().stopMonitorMemoryInfo();
                break;
            case TYPE_FRAME:
                PerformanceDataManager.getInstance().stopMonitorFrameInfo();
                break;
            default:
                break;
        }
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
    }
}
