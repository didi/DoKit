package com.didichuxing.doraemonkit.datapick;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.didichuxing.doraemonkit.BuildConfig;
import com.didichuxing.doraemonkit.constant.DokitConstant;

import java.util.List;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020-02-18-15:40
 * 描    述：数据指标监控
 * 修订历史：
 * ================================================
 */
public class DataPickBean {
    /**
     * 基础信息
     */
    private String platform;
    private String pId;
    /**
     * 埋点上传时间
     */
    private String time;
    private String phoneMode;
    /**
     * 系统版本
     */
    private String systemVersion;
    private String appName;
    /**
     * 包名
     */
    private String appId;
    private String dokitVersion;
    private List<EventBean> events;

    DataPickBean() {
        //初始化基础数据
        this.pId = DokitConstant.PRODUCT_ID;
        this.appName = AppUtils.getAppName();
        this.appId = AppUtils.getAppPackageName();
        this.dokitVersion = BuildConfig.DOKIT_VERSION;
        this.platform = "Android";
        this.phoneMode = DeviceUtils.getModel();
        this.time = "" + TimeUtils.getNowMills();
        this.systemVersion = DeviceUtils.getSDKVersionName();
    }

    void setEvents(List<EventBean> events) {
        this.events = events;
    }

    public static class EventBean {

        /**
         * 数据名称
         */
        private String eventName;
        /**
         * 埋点记录时间
         */
        private String time;

        EventBean(String eventName) {
            this.eventName = eventName;
            this.time = "" + TimeUtils.getNowMills();
        }


        String getTime() {
            return time;
        }


        @Override
        public String toString() {
            return "EventBean{" +
                    ", eventName='" + eventName + '\'' +
                    ", time=" + time +
                    '}';
        }
    }


}
