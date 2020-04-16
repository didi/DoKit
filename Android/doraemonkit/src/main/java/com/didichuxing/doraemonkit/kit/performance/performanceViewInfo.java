package com.didichuxing.doraemonkit.kit.performance;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-10-14-11:15
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class performanceViewInfo {
    int performanceType;
    String title;
    int interval;

    public performanceViewInfo(int performanceType, String title, int interval) {
        this.performanceType = performanceType;
        this.title = title;
        this.interval = interval;
    }


    public int getPerformanceType() {
        return performanceType;
    }

    public String getTitle() {
        return title;
    }

    public int getInterval() {
        return interval;
    }
}
