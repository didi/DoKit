package com.didichuxing.doraemonkit.plugin;

import com.android.build.gradle.AppExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/3/24-14:58
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class DokitExtUtil {
    private String mApplicationId;
    /**
     * dokit 插件开关 字段权限必须为public 否则无法进行赋值
     */
    private boolean mDokitPluginSwitch = true;
    private List<String> applications = new ArrayList<>();
    private DokitExtension.CommConfig commConfig = new DokitExtension.CommConfig();
    private DokitExtension.SlowMethodConfig slowMethodConfig = new DokitExtension.SlowMethodConfig();


    public boolean dokitPluginSwitchOpen() {
        return mDokitPluginSwitch;
    }

    public DokitExtension.CommConfig getCommConfig() {
        return commConfig;
    }

    public DokitExtension.SlowMethodConfig getSlowMethodConfig() {
        return slowMethodConfig;
    }

    /**
     * 静态内部类单例
     */
    private static class Holder {
        private static DokitExtUtil INSTANCE = new DokitExtUtil();
    }

    public static DokitExtUtil getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 初始化
     *
     * @param dokitExtension dokitExtension
     * @param appExtension   appExtension
     */
    public void init(DokitExtension dokitExtension, AppExtension appExtension) {
        if (dokitExtension != null) {
            this.mDokitPluginSwitch = dokitExtension.dokitPluginSwitch;
            //设置普通的配置
            this.commConfig = dokitExtension.comm;

            this.slowMethodConfig.strategy = dokitExtension.slowMethod.strategy;
            this.slowMethodConfig.methodSwitch = dokitExtension.slowMethod.methodSwitch;
            /**
             * ============慢函数普通策略的配置==========
             */
            this.slowMethodConfig.normalMethodConfig.thresholdTime = dokitExtension.slowMethod.normalMethodConfig.thresholdTime;
            //设置慢函数普通策略插装包名
            this.slowMethodConfig.normalMethodConfig.packageNames.clear();
            for (String packageName : dokitExtension.slowMethod.normalMethodConfig.packageNames) {
                this.slowMethodConfig.normalMethodConfig.packageNames.add(packageName.replaceAll("\\.", "/"));
            }
            //添加默认的包名
            String applicationId = appExtension.getDefaultConfig().getApplicationId().replaceAll("\\.", "/");
            if (this.slowMethodConfig.normalMethodConfig.packageNames.isEmpty()) {
                this.slowMethodConfig.normalMethodConfig.packageNames.add(applicationId);
            }

            //设置慢函数普通策略插装包名黑名单
            this.slowMethodConfig.normalMethodConfig.methodBlacklist.clear();
            for (String blackStr : dokitExtension.slowMethod.normalMethodConfig.methodBlacklist) {
                this.slowMethodConfig.normalMethodConfig.methodBlacklist.add(blackStr.replaceAll("\\.", "/"));
            }
            /**
             * ============慢函数普通策略的配置==========
             */

            /**
             * ============慢函数stack策略的配置==========
             */
            this.slowMethodConfig.stackMethodConfig.thresholdTime = dokitExtension.slowMethod.stackMethodConfig.thresholdTime;
            this.slowMethodConfig.stackMethodConfig.enterMethods.clear();
            for (String methodName : dokitExtension.slowMethod.stackMethodConfig.enterMethods) {
                this.slowMethodConfig.stackMethodConfig.enterMethods.add(methodName.replaceAll("\\.", "/"));
            }
            //添加默认的入口函数
            if (this.slowMethodConfig.normalMethodConfig.packageNames.isEmpty()) {
                for (String application : applications) {
                    String attachBaseContextMethodName = application + "/attachBaseContext";
                    String onCreateMethodName = application + "/onCreate";
                    this.slowMethodConfig.stackMethodConfig.enterMethods.add(attachBaseContextMethodName);
                    this.slowMethodConfig.stackMethodConfig.enterMethods.add(onCreateMethodName);
                }
            }
            /**
             * ============慢函数stack策略的配置==========
             */
        }
    }

    void setApplications(List<String> applications) {

        if (applications.isEmpty()) {
            return;
        }
        this.applications.clear();
        for (String application : applications) {
            this.applications.add(application.replaceAll("\\.", "/"));
        }

    }


    public boolean ignorePackageNames(String className) {
        boolean isMatched = false;
        for (String packageName : ignorePackageNames) {
            if (className.contains(packageName)) {
                isMatched = true;
                break;
            }
        }
        return isMatched;
    }

    private String[] ignorePackageNames = new String[]{
            "com/didichuxing/doraemonkit/aop",
            "com/didichuxing/doraemonkit/kit",
            "com/didichuxing/doraemonkit/okgo",
            "com/didichuxing/doraemonkit/datapick",
            "com/didichuxing/doraemonkit/reflection",
            "com/didichuxing/doraemonkit/zxing"
    };

}
