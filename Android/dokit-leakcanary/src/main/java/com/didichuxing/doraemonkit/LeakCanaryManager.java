package com.didichuxing.doraemonkit;

import android.app.Application;
import android.util.Log;

import com.didichuxing.doraemonkit.util.ActivityUtils;
import com.didichuxing.doraemonkit.abridge.AbridgeCallBack;
import com.didichuxing.doraemonkit.abridge.IBridge;
import com.didichuxing.doraemonkit.constant.DokitConstant;
import com.didichuxing.doraemonkit.kit.health.AppHealthInfoUtil;
import com.didichuxing.doraemonkit.kit.health.model.AppHealthInfo;
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
    private static final String TAG = "LeakCanaryManager";

    public static void install(Application app) {
        if (LeakCanary.isInAnalyzerProcess(app)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(app);
    }

    /**
     * 初始化跨进程框架
     * 接受leakcanary 进程泄漏传递过来的数据
     */
    public static void initAidlBridge(Application application) {
        if (!DokitConstant.APP_HEALTH_RUNNING) {
            return;
        }
        IBridge.init(application, application.getPackageName(), IBridge.AbridgeType.AIDL);
        IBridge.registerAIDLCallBack(new AbridgeCallBack() {
            @Override
            public void receiveMessage(String message) {
                try {
                    Log.i(TAG, "====aidl=====>" + message);
                    if (DokitConstant.APP_HEALTH_RUNNING) {
                        AppHealthInfo.DataBean.LeakBean leakBean = new AppHealthInfo.DataBean.LeakBean();
                        leakBean.setPage(ActivityUtils.getTopActivity().getClass().getCanonicalName());
                        leakBean.setDetail(message);
                        AppHealthInfoUtil.getInstance().addLeakInfo(leakBean);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }
}
