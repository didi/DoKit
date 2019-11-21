package com.didichuxing.doraemonkit;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-10-17-10:11
 * 描    述：
 * 修订历史：
 * ================================================
 */
class LeakCanaryManager {
    public static void install(Application app) {
        if (LeakCanary.isInAnalyzerProcess(app)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(app);
    }
}
