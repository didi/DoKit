package com.didichuxing.doraemonkit.plugin;

import com.android.build.gradle.AppExtension;
import com.didichuxing.doraemonkit.plugin.extension.CommExt;
import com.didichuxing.doraemonkit.plugin.extension.DoKitExt;
import com.didichuxing.doraemonkit.plugin.extension.SlowMethodExt;

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
public class DoKitExtUtil {
    private String mApplicationId;
    /**
     * dokit 插件开关 字段权限必须为public 否则无法进行赋值
     */
    private boolean mDokitPluginSwitch = true;
    private boolean mDokitLogSwitch = false;
    private List<String> applications = new ArrayList<>();
    private CommExt commExt = new CommExt();
    private SlowMethodExt slowMethodExt = new SlowMethodExt();


    public boolean dokitPluginSwitchOpen() {
        return mDokitPluginSwitch;
    }

    public boolean dokitLogSwitchOpen() {
        return mDokitLogSwitch;
    }

    public CommExt getCommExt() {
        return commExt;
    }

    public SlowMethodExt getSlowMethodExt() {
        return slowMethodExt;
    }

    /**
     * 静态内部类单例
     */
    private static class Holder {
        private static DoKitExtUtil INSTANCE = new DoKitExtUtil();
    }

    public static DoKitExtUtil getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 初始化
     *
     * @param dokitExtension dokitExtension
     * @param appExtension   appExtension
     */
    public void init(DoKitExt dokitExtension, AppExtension appExtension) {
        if (dokitExtension != null) {
            this.mDokitPluginSwitch = dokitExtension.dokitPluginSwitch;
            this.mDokitLogSwitch = dokitExtension.dokitLogSwitch;
            //设置普通的配置
            this.commExt = dokitExtension.comm;

            this.slowMethodExt.strategy = dokitExtension.slowMethod.strategy;
            this.slowMethodExt.methodSwitch = dokitExtension.slowMethod.methodSwitch;
            /**
             * ============慢函数普通策略的配置 start==========
             */
            this.slowMethodExt.normalMethod.thresholdTime = dokitExtension.slowMethod.normalMethod.thresholdTime;
            //设置慢函数普通策略插装包名
            this.slowMethodExt.normalMethod.packageNames.clear();
            for (String packageName : dokitExtension.slowMethod.normalMethod.packageNames) {
                this.slowMethodExt.normalMethod.packageNames.add(packageName.replaceAll("\\.", "/"));
            }
            //添加默认的包名
            if (appExtension.getDefaultConfig() != null && appExtension.getDefaultConfig().getApplicationId() != null) {
                String applicationId = appExtension.getDefaultConfig().getApplicationId().replaceAll("\\.", "/");
                if (this.slowMethodExt.normalMethod.packageNames.isEmpty()) {
                    this.slowMethodExt.normalMethod.packageNames.add(applicationId);
                }
            }


            //设置慢函数普通策略插装包名黑名单
            this.slowMethodExt.normalMethod.methodBlacklist.clear();
            for (String blackStr : dokitExtension.slowMethod.normalMethod.methodBlacklist) {
                this.slowMethodExt.normalMethod.methodBlacklist.add(blackStr.replaceAll("\\.", "/"));
            }
            /**
             * ============慢函数普通策略的配置end==========
             */

            /**
             * ============慢函数stack策略的配置 start==========
             */
            this.slowMethodExt.stackMethod.thresholdTime = dokitExtension.slowMethod.stackMethod.thresholdTime;
            this.slowMethodExt.stackMethod.enterMethods.clear();
            //添加默认的入口函数
            for (String application : applications) {
                String attachBaseContextMethodName = application + "/attachBaseContext";
                String onCreateMethodName = application + "/onCreate";
                this.slowMethodExt.stackMethod.enterMethods.add(attachBaseContextMethodName);
                this.slowMethodExt.stackMethod.enterMethods.add(onCreateMethodName);
            }

            for (String methodName : dokitExtension.slowMethod.stackMethod.enterMethods) {
                this.slowMethodExt.stackMethod.enterMethods.add(methodName.replaceAll("\\.", "/"));
            }

            /**
             * ============慢函数stack策略的配置  end==========
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


    public void log(String tag, String className, String methodName, int access, String desc, String signature, int thresholdTime) {
        if (mDokitLogSwitch) {
            System.out.println(tag + "===matched====>" + "  className===" + className + "   methodName===" + methodName + "   access===" + access + "   desc===" + desc + "   signature===" + signature + "    thresholdTime===" + thresholdTime);
        }
    }

}
