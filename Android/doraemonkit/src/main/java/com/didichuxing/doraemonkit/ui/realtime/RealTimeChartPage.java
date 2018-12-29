package com.didichuxing.doraemonkit.ui.realtime;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.didichuxing.doraemonkit.ui.base.BaseFloatPage;
import com.didichuxing.doraemonkit.ui.base.FloatPageManager;
import com.didichuxing.doraemonkit.ui.base.PageIntent;
import com.didichuxing.doraemonkit.ui.realtime.widget.LineChart;
import com.didichuxing.doraemonkit.ui.realtime.datasource.DataSourceFactory;
import com.didichuxing.doraemonkit.ui.realtime.datasource.IDataSource;
import com.didichuxing.doraemonkit.util.UIUtils;


/**
 * @desc: cpu、内存、流量监控的实时图，需要自定义IDataSource接口作为数据源。跳转请直接调用{@link #openChartPage(String, int, int, OnFloatPageChangeListener)}
 */
public class RealTimeChartPage extends BaseFloatPage {
    public static final String TAG = "RealTimeChartPage";
    public static final String KEY_TYPE = "type";
    public static final String KEY_TITLE = "title";
    public static final String KEY_INTERVAL = "interval";
    public static final int DEFAULT_REFRESH_INTERVAL = 1000;

    private LineChart mLineChart;

    @Override
    protected View onCreateView(Context context, ViewGroup view) {
        mLineChart = new LineChart(context);
        return mLineChart;
    }

    @Override
    protected void onViewCreated(View view) {
        super.onViewCreated(view);
        init();
    }

    public void init() {
        String title = getBundle().getString(KEY_TITLE);
        int type = getBundle().getInt(KEY_TYPE);
        int interval = getBundle().getInt(KEY_INTERVAL, DEFAULT_REFRESH_INTERVAL);

        IDataSource dataSource = DataSourceFactory.createDataSource(type);
        mLineChart.setTitle(title);
        mLineChart.setInterval(interval);
        mLineChart.setDataSource(dataSource);
        mLineChart.startMove();
    }

    @Override
    protected void onLayoutParamsCreated(WindowManager.LayoutParams params) {
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = UIUtils.dp2px(getContext(), 240);
    }

    @Override
    protected boolean onBackPressed() {
        return false;
    }

    /**
     * 打开实时折线图浮窗
     *
     * @param title    左上角标题
     * @param type     类型，需要在{@link DataSourceFactory} 实现数据源接口
     * @param interval 刷新间隔,单位毫秒
     */
    public static void openChartPage(String title, int type, int interval, OnFloatPageChangeListener listener) {
        if (updateChartPage(title, type, interval, listener)) {
            return;
        }
        closeChartPage();
        PageIntent pageIntent = new PageIntent(RealTimeChartPage.class);
        pageIntent.mode = PageIntent.MODE_SINGLE_INSTANCE;
        pageIntent.tag = RealTimeChartPage.TAG;
        Bundle bundle = new Bundle();
        bundle.putString(RealTimeChartPage.KEY_TITLE, title);
        bundle.putInt(RealTimeChartPage.KEY_TYPE, type);
        bundle.putInt(RealTimeChartPage.KEY_INTERVAL, interval);
        pageIntent.bundle = bundle;
        FloatPageManager.getInstance().add(pageIntent);
        RealTimeChartIconPage.openChartIconPage(type, listener);
    }

    /**
     * 当前page已经打开的情况下，只做更新，防止闪烁
     *
     * @param title
     * @param type
     * @param interval
     * @param listener
     * @return
     */
    private static boolean updateChartPage(String title, int type, int interval, OnFloatPageChangeListener listener) {
        RealTimeChartPage chartPage = (RealTimeChartPage) FloatPageManager.getInstance().getFloatPage(RealTimeChartPage.TAG);
        RealTimeChartIconPage chartIconPage = (RealTimeChartIconPage) FloatPageManager.getInstance().getFloatPage(RealTimeChartIconPage.TAG);
        if (chartIconPage == null || chartPage == null) {
            return false;
        }
        Bundle bundle = new Bundle();
        bundle.putString(RealTimeChartPage.KEY_TITLE, title);
        bundle.putInt(RealTimeChartPage.KEY_TYPE, type);
        bundle.putInt(RealTimeChartPage.KEY_INTERVAL, interval);
        chartPage.setBundle(bundle);
        chartPage.init();

        bundle = new Bundle();
        bundle.putInt(RealTimeChartPage.KEY_TYPE, type);
        chartIconPage.setBundle(bundle);
        chartIconPage.setListener(listener);
        return true;
    }

    public static void closeChartPage() {
        FloatPageManager.getInstance().remove(RealTimeChartPage.TAG);
        FloatPageManager.getInstance().remove(RealTimeChartIconPage.TAG);
    }

    public static void removeCloseListener() {
        RealTimeChartIconPage page = (RealTimeChartIconPage) FloatPageManager.getInstance().getFloatPage(RealTimeChartIconPage.TAG);
        if (page != null) {
            page.setListener(null);
        }
    }

    @Override
    public void onEnterForeground() {
        super.onEnterForeground();
        mLineChart.startMove();
        getRootView().setVisibility(View.VISIBLE);
    }

    @Override
    public void onEnterBackground() {
        super.onEnterBackground();
        mLineChart.stopMove();
        getRootView().setVisibility(View.GONE);
    }
}
