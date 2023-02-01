package com.didichuxing.doraemonkit.datapick;

import com.didichuxing.doraemonkit.util.AppUtils;
import com.didichuxing.doraemonkit.util.DeviceUtils;
import com.didichuxing.doraemonkit.util.TimeUtils;
import com.didichuxing.doraemonkit.BuildConfig;
import com.didichuxing.doraemonkit.kit.core.DoKitManager;

import java.util.List;
import java.util.Locale;

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
    /**
     * app 或者系统语言
     */
    private String language;
    private List<EventBean> events;

    DataPickBean() {
        //初始化基础数据
        this.pId = DoKitManager.PRODUCT_ID;
        this.appName = AppUtils.getAppName();
        this.appId = AppUtils.getAppPackageName();
        this.dokitVersion = BuildConfig.DOKIT_VERSION;
        this.platform = "Android";
        this.phoneMode = DeviceUtils.getModel();
        this.time = "" + TimeUtils.getNowMills();
        this.systemVersion = DeviceUtils.getSDKVersionName();
        this.language = Locale.getDefault().getDisplayLanguage();
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
        /**
         * 页面ID
         */
        private String pageId;
        /**
         * 业务专区名称/功能名称
         */
        private String businessName;

        EventBean(String eventName, String pageId, String businessName) {
            this.eventName = eventName;
            this.pageId = pageId;
            this.businessName = businessName;
            this.time = "" + TimeUtils.getNowMills();
        }


        String getTime() {
            return time;
        }

        @Override
        public String toString() {
            return "EventBean{" +
                "eventName='" + eventName + '\'' +
                ", time='" + time + '\'' +
                ", pageId='" + pageId + '\'' +
                ", businessName='" + businessName + '\'' +
                '}';
        }
    }

    @Override
    public String toString() {
        return "DataPickBean{" +
                "platform='" + platform + '\'' +
                ", pId='" + pId + '\'' +
                ", time='" + time + '\'' +
                ", phoneMode='" + phoneMode + '\'' +
                ", systemVersion='" + systemVersion + '\'' +
                ", appName='" + appName + '\'' +
                ", appId='" + appId + '\'' +
                ", dokitVersion='" + dokitVersion + '\'' +
                ", language='" + language + '\'' +
                ", events=" + events +
                '}';
    }


}
