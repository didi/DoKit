package com.didichuxing.doraemonkit.datapick;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.didichuxing.doraemonkit.BuildConfig;

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
    private long time;
    private String phoneMode;
    private String systemVersion;
    private String appName;
    private String appVersion;
    private String dokitVersion;

    /**
     * 埋点信息
     */
    //eventType click pv uv
    private String eventType;
    /**
     * 数据名称
     */
    private String name;
    /**
     * 工具分组
     */
    private String kitGroup;

    public DataPickBean(String eventType, String kitGroup, String name) {
        //初始化基础数据
        this.appName = AppUtils.getAppName();
        this.appVersion = AppUtils.getAppVersionName();
        this.dokitVersion = BuildConfig.DOKIT_VERSION;
        this.platform = "Android";
        this.phoneMode = DeviceUtils.getModel();
        this.time = TimeUtils.getNowMills();
        this.systemVersion = DeviceUtils.getSDKVersionName();
        this.eventType = eventType;
        this.kitGroup = kitGroup;
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "DataPickBean{" +
                "platform='" + platform + '\'' +
                ", time='" + time + '\'' +
                ", phoneMode='" + phoneMode + '\'' +
                ", systemVersion='" + systemVersion + '\'' +
                ", appName='" + appName + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", dokitVersion='" + dokitVersion + '\'' +
                ", eventType='" + eventType + '\'' +
                ", name='" + name + '\'' +
                ", kitGroup='" + kitGroup + '\'' +
                '}';
    }
}
