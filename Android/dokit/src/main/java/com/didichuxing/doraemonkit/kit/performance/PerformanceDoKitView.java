package com.didichuxing.doraemonkit.kit.performance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.didichuxing.doraemonkit.DoKit;
import com.didichuxing.doraemonkit.util.ActivityUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.DokitMemoryConfig;
import com.didichuxing.doraemonkit.kit.core.AbsDoKitView;
import com.didichuxing.doraemonkit.kit.core.DoKitViewLayoutParams;
import com.didichuxing.doraemonkit.kit.performance.datasource.DataSourceFactory;
import com.didichuxing.doraemonkit.kit.performance.datasource.IDataSource;
import com.didichuxing.doraemonkit.kit.performance.widget.LineChart;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-10-11-16:05
 * 描    述：性能监控 帧率、 CPU、RAM、流量监控统一显示的DokitView
 * 修订历史：
 * ================================================
 */
public class PerformanceDoKitView extends AbsDoKitView implements PerformanceCloseListener {
    static final int DEFAULT_REFRESH_INTERVAL = 1000;
    PerformanceCloseDoKitView mPerformanceCloseDokitView;
    LinearLayout mPerformanceWrap;
    FrameLayout mFlWrap0, mFlWrap1, mFlWrap2, mFlWrap3;
    LineChart mLineChart0, mLineChart1, mLineChart2, mLineChart3;
    ImageView mIvClose0, mIvClose1, mIvClose2, mIvClose3;
    private PerformanceFragmentCloseListener mPerformanceFragmentCloseListener;

    /**
     * 添加性能检测页面的浮标关闭监听
     *
     * @param listener
     */
    void addPerformanceFragmentCloseListener(PerformanceFragmentCloseListener listener) {
        this.mPerformanceFragmentCloseListener = listener;
    }

    /**
     * 移除性能检测页面的浮标关闭监听
     *
     * @param listener
     */
    void removePerformanceFragmentCloseListener(PerformanceFragmentCloseListener listener) {
        if (mPerformanceFragmentCloseListener != null && mPerformanceFragmentCloseListener == listener) {
            mPerformanceFragmentCloseListener = null;
        }
    }


    @Override
    public void onCreate(Context context) {

    }

    @Override
    public View onCreateView(Context context, FrameLayout rootView) {
        return LayoutInflater.from(context).inflate(R.layout.dk_performance_wrap, rootView, false);
    }

    /**
     * 动态添加性能项目
     *
     * @param performanceType
     * @param title
     * @param interval
     */
    void addItem(int performanceType, String title, int interval) {
        if (mPerformanceWrap == null) {
            return;
        }
        int needOperateViewIndex = -1;
        for (int index = 0; index < mPerformanceWrap.getChildCount(); index++) {
            if (mPerformanceWrap.getChildAt(index).getVisibility() == View.GONE) {
                needOperateViewIndex = index;
                break;
            }
        }

        if (needOperateViewIndex == -1) {
            return;
        }

        FrameLayout needOperateViewWrap = (FrameLayout) mPerformanceWrap.getChildAt(needOperateViewIndex);
        needOperateViewWrap.setVisibility(View.VISIBLE);
        LineChart needOperateLineChart = needOperateViewWrap.findViewWithTag("lineChart");

        IDataSource dataSource = DataSourceFactory.createDataSource(performanceType);
        needOperateLineChart.setPerformanceType(performanceType);
        needOperateLineChart.setTitle(title);
        needOperateLineChart.setInterval(interval);
        needOperateLineChart.setDataSource(dataSource);
        needOperateLineChart.startMove();
        //系统模式下添加关闭按钮
        if (!isNormalMode() && mPerformanceCloseDokitView != null) {
            mPerformanceCloseDokitView.addItem(needOperateViewIndex, performanceType);
        }

    }

    void removeItem(int performanceType) {
        if (mPerformanceWrap == null) {
            return;
        }
        int needOperateViewIndex = -1;
        for (int index = 0; index < mPerformanceWrap.getChildCount(); index++) {
            if (mPerformanceWrap.getChildAt(index).getVisibility() != View.GONE) {
                LineChart needOperateLineChart = mPerformanceWrap.getChildAt(index).findViewWithTag("lineChart");
                if (needOperateLineChart.getPerformanceType() == performanceType) {
                    needOperateViewIndex = index;
                    break;
                }

            }
        }
        if (needOperateViewIndex == -1) {
            return;
        }

        FrameLayout frameLayout = (FrameLayout) mPerformanceWrap.getChildAt(needOperateViewIndex);
        frameLayout.setVisibility(View.GONE);
        LineChart needOperateLineChart = frameLayout.findViewWithTag("lineChart");
        needOperateLineChart.stopMove();
        needOperateLineChart.setPerformanceType(-1);
        switch (performanceType) {
            case DataSourceFactory.TYPE_FPS:
                DokitMemoryConfig.FPS_STATUS = false;
                break;
            case DataSourceFactory.TYPE_CPU:
                DokitMemoryConfig.CPU_STATUS = false;
                break;
            case DataSourceFactory.TYPE_RAM:
                DokitMemoryConfig.RAM_STATUS = false;
                break;
            case DataSourceFactory.TYPE_NETWORK:
                DokitMemoryConfig.NETWORK_STATUS = false;
                break;
            default:
                break;
        }

        //系统模式下添加关闭按钮
        if (!isNormalMode() && mPerformanceCloseDokitView != null) {
            mPerformanceCloseDokitView.removeItem(needOperateViewIndex);
        }

    }


    @Override
    public void onViewCreated(FrameLayout rootView) {
        mPerformanceWrap = findViewById(R.id.ll_performance_wrap);
        mFlWrap0 = findViewById(R.id.fl_chart0);
        mFlWrap0.setVisibility(View.GONE);
        mFlWrap1 = findViewById(R.id.fl_chart1);
        mFlWrap1.setVisibility(View.GONE);
        mFlWrap2 = findViewById(R.id.fl_chart2);
        mFlWrap2.setVisibility(View.GONE);
        mFlWrap3 = findViewById(R.id.fl_chart3);
        mFlWrap3.setVisibility(View.GONE);
        mLineChart0 = findViewById(R.id.linechart0);
        mLineChart1 = findViewById(R.id.linechart1);
        mLineChart2 = findViewById(R.id.linechart2);
        mLineChart3 = findViewById(R.id.linechart3);
        mIvClose0 = findViewById(R.id.iv_close0);
        mIvClose1 = findViewById(R.id.iv_close1);
        mIvClose2 = findViewById(R.id.iv_close2);
        mIvClose3 = findViewById(R.id.iv_close3);
        setDoKitViewNotResponseTouchEvent(getDoKitView());
        setDoKitViewNotResponseTouchEvent(mLineChart0);
        setDoKitViewNotResponseTouchEvent(mLineChart1);
        setDoKitViewNotResponseTouchEvent(mLineChart2);
        setDoKitViewNotResponseTouchEvent(mLineChart3);
        if (isNormalMode()) {
            mIvClose0.setVisibility(View.VISIBLE);
            mIvClose1.setVisibility(View.VISIBLE);
            mIvClose2.setVisibility(View.VISIBLE);
            mIvClose3.setVisibility(View.VISIBLE);
        } else {
            mIvClose0.setVisibility(View.GONE);
            mIvClose1.setVisibility(View.GONE);
            mIvClose2.setVisibility(View.GONE);
            mIvClose3.setVisibility(View.GONE);
        }
        mIvClose0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LineChart lineChart = ((FrameLayout) v.getParent()).findViewWithTag("lineChart");
                onClose(lineChart.getPerformanceType());
            }
        });
        mIvClose1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LineChart lineChart = ((FrameLayout) v.getParent()).findViewWithTag("lineChart");
                onClose(lineChart.getPerformanceType());
            }
        });
        mIvClose2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LineChart lineChart = ((FrameLayout) v.getParent()).findViewWithTag("lineChart");
                onClose(lineChart.getPerformanceType());
            }
        });
        mIvClose3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LineChart lineChart = ((FrameLayout) v.getParent()).findViewWithTag("lineChart");
                onClose(lineChart.getPerformanceType());
            }
        });
    }

    @Override
    public void initDokitViewLayoutParams(DoKitViewLayoutParams params) {
        params.flags = DoKitViewLayoutParams.FLAG_NOT_FOCUSABLE_AND_NOT_TOUCHABLE;
        params.width = DoKitViewLayoutParams.MATCH_PARENT;
        params.height = DoKitViewLayoutParams.MATCH_PARENT;
    }

    @Override
    public boolean canDrag() {
        return false;
    }

    /**
     * 系统模式下显示单独的关闭按钮
     */
    private void showSystemPerfoemanceCloseDokitView() {
        DoKit.launchFloating(PerformanceCloseDoKitView.class);
        mPerformanceCloseDokitView = DoKit.getDoKitView(ActivityUtils.getTopActivity(), PerformanceCloseDoKitView.class);
        if (mPerformanceCloseDokitView != null) {
            mPerformanceCloseDokitView.setPerformanceCloseListener(PerformanceDoKitView.this);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        //系统模式下主动添加关闭按钮
        if (!isNormalMode()) {
            showSystemPerfoemanceCloseDokitView();
        }

        //普通模式下自己处理页面切换
        if (isNormalMode()) {
            hideAllPerformanceView();
            for (performanceViewInfo performanceViewInfo : PerformanceDokitViewManager.singleperformanceViewInfos.values()) {
                PerformanceDokitViewManager.open(performanceViewInfo.performanceType, performanceViewInfo.title, null);
            }
        }
    }

    @Override
    public void onClose(int performanceType) {
        if (performanceType == -1) {
            return;
        }
        /**
         *点击关闭按钮 回调switch按钮关闭
         */
        if (mPerformanceFragmentCloseListener != null) {
            mPerformanceFragmentCloseListener.onClose(performanceType);
        }

        PerformanceDokitViewManager.close(performanceType, PerformanceDokitViewManager.getTitleByPerformanceType(getContext(), performanceType));
    }


    @Override
    public void onEnterForeground() {
        super.onEnterForeground();
        if (((FrameLayout) mLineChart0.getParent()).getVisibility() == View.VISIBLE) {
            mLineChart0.startMove();
        }
        if (((FrameLayout) mLineChart1.getParent()).getVisibility() == View.VISIBLE) {
            mLineChart1.startMove();
        }
        if (((FrameLayout) mLineChart2.getParent()).getVisibility() == View.VISIBLE) {
            mLineChart2.startMove();
        }
        if (((FrameLayout) mLineChart3.getParent()).getVisibility() == View.VISIBLE) {
            mLineChart3.startMove();
        }
    }

    @Override
    public void onEnterBackground() {
        super.onEnterBackground();
        mLineChart0.stopMove();
        mLineChart1.stopMove();
        mLineChart2.stopMove();
        mLineChart3.stopMove();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mPerformanceFragmentCloseListener = null;
        mLineChart0.stopMove();
        mLineChart0 = null;
        mLineChart1.stopMove();
        mLineChart1 = null;
        mLineChart2.stopMove();
        mLineChart2 = null;
        mLineChart3.stopMove();
        mLineChart3 = null;
    }

    /**
     * 隐藏所有的
     */
    private void hideAllPerformanceView() {
        if (!isNormalMode()) {
            return;
        }
        mFlWrap0.setVisibility(View.GONE);
        mFlWrap1.setVisibility(View.GONE);
        mFlWrap2.setVisibility(View.GONE);
        mFlWrap3.setVisibility(View.GONE);
    }


}
