package com.didichuxing.doraemonkit.kit.performance;

import android.content.Context;

import com.didichuxing.doraemonkit.DoKit;
import com.didichuxing.doraemonkit.util.ActivityUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.performance.datasource.DataSourceFactory;

import java.util.TreeMap;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-10-11-16:05
 * 描    述：性能监控 帧率、 CPU、RAM、流量监控统一显示的DokitView 管理类
 * 修订历史：
 * ================================================
 */
public class PerformanceDokitViewManager {

    public static TreeMap<String, performanceViewInfo> singleperformanceViewInfos = new TreeMap<>();

    /**
     * @param performanceType 参考 DataSourceFactory
     */
    public static void open(int performanceType, String title, PerformanceFragmentCloseListener listener) {
        open(performanceType, title, PerformanceDoKitView.DEFAULT_REFRESH_INTERVAL, listener);
    }

    public static void open(int performanceType, String title, int interval, PerformanceFragmentCloseListener listener) {
        PerformanceDoKitView performanceDokitView = DoKit.getDoKitView(ActivityUtils.getTopActivity(), PerformanceDoKitView.class);
        if (performanceDokitView == null) {
            DoKit.launchFloating(PerformanceDoKitView.class);
            performanceDokitView = DoKit.getDoKitView(ActivityUtils.getTopActivity(), PerformanceDoKitView.class);
            performanceDokitView.addItem(performanceType, title, interval);
        } else {
            performanceDokitView.addItem(performanceType, title, interval);
        }
        performanceDokitView.addPerformanceFragmentCloseListener(listener);
        singleperformanceViewInfos.put(title, new performanceViewInfo(performanceType, title, interval));
    }

    /**
     * 性能检测设置页面关闭时调用
     *
     * @param listener
     */
    public static void onPerformanceSettingFragmentDestroy(PerformanceFragmentCloseListener listener) {
        PerformanceDoKitView performanceDokitView = DoKit.getDoKitView(ActivityUtils.getTopActivity(), PerformanceDoKitView.class);
        if (performanceDokitView != null) {
            performanceDokitView.removePerformanceFragmentCloseListener(listener);
        }
    }

    /**
     * @param performanceType 参考 DataSourceFactory
     */
    public static void close(int performanceType, String title) {
        PerformanceDoKitView performanceDokitView = DoKit.getDoKitView(ActivityUtils.getTopActivity(), PerformanceDoKitView.class);
        if (performanceDokitView != null) {
            performanceDokitView.removeItem(performanceType);
        }

        singleperformanceViewInfos.remove(title);
    }


    public static String getTitleByPerformanceType(Context context, int performanceType) {
        String title = "";
        switch (performanceType) {
            case DataSourceFactory.TYPE_FPS:
                title = context.getString(R.string.dk_kit_frame_info_desc);
                break;
            case DataSourceFactory.TYPE_CPU:
                title = context.getString(R.string.dk_frameinfo_cpu);
                break;
            case DataSourceFactory.TYPE_RAM:
                title = context.getString(R.string.dk_ram_detection_title);
                break;
            case DataSourceFactory.TYPE_NETWORK:
                title = context.getString(R.string.dk_kit_net_monitor);
                break;
            default:
                break;
        }
        return title;
    }


}
