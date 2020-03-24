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
public class SlowMethodUtil {
    private boolean showMethodSwitch = true;
    private List<String> packageNames = new ArrayList<>();

    public boolean isShowMethodSwitch() {
        return showMethodSwitch;
    }


    public List<String> getPackageNames() {
        return packageNames;
    }

    public int getThresholdTime() {
        return thresholdTime;
    }


    /**
     * 单位为500ms
     */
    private int thresholdTime = 500;

    /**
     * 静态内部类单例
     */
    private static class Holder {
        private static SlowMethodUtil INSTANCE = new SlowMethodUtil();
    }

    public static SlowMethodUtil getInstance() {
        return Holder.INSTANCE;
    }

    public boolean needUpdate(DokitExtension dokitExtension) {
        if (dokitExtension.packageNames.size() != packageNames.size() || dokitExtension.slowMethodSwitch != showMethodSwitch || dokitExtension.thresholdTime != thresholdTime) {
            return true;
        }

        return false;
    }

    public synchronized void init(AppExtension appExtension, DokitExtension dokitExtension) {
        List<String> innerPackageNames = new ArrayList<>();
        //需要将applicationId中的 .替换为/ 因为字节码中会把.转化为/
        String applicationId = appExtension.getDefaultConfig().getApplicationId().replaceAll("\\.", "/");
        if (dokitExtension != null) {
            showMethodSwitch = dokitExtension.slowMethodSwitch;
            innerPackageNames = dokitExtension.packageNames;
            thresholdTime = dokitExtension.thresholdTime;
            if (innerPackageNames.isEmpty() && !StringUtils.isEmpty(applicationId)) {
                innerPackageNames.add(applicationId);
            }
        } else {
            if (!StringUtils.isEmpty(applicationId)) {
                innerPackageNames.add(applicationId);
            }
        }

        packageNames.clear();

        for (String packageName : innerPackageNames) {
            packageName = packageName.replaceAll("\\.", "/");
            packageNames.add(packageName);
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
            "com/didichuxing/doraemonkit/kit/methodtrace",
            "com/didichuxing/doraemonkit/kit/network",
            "com/didichuxing/doraemonkit/kit/timecounter",
            "com/didichuxing/doraemonkit/okgo",
            "com/didichuxing/doraemonkit/datapick"
    };

}
