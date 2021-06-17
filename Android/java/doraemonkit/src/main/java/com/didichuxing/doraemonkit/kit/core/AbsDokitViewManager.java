package com.didichuxing.doraemonkit.kit.core;

import android.app.Activity;

import com.didichuxing.doraemonkit.constant.DoKitConstant;
import com.didichuxing.doraemonkit.constant.DoKitModule;
import com.didichuxing.doraemonkit.constant.WSMode;
import com.didichuxing.doraemonkit.kit.health.CountDownDokitView;

import java.util.HashMap;
import java.util.Map;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-09-28-15:18
 * 描    述：页面浮标管理类接口
 * 修订历史：
 * ================================================
 */
public abstract class AbsDokitViewManager implements DokitViewManagerInterface {

    protected String TAG = this.getClass().getSimpleName();

    /**
     * 添加倒计时DokitView
     */
    void attachCountDownDokitView(Activity activity) {
        if (!DoKitConstant.APP_HEALTH_RUNNING) {
            return;
        }
        if (activity instanceof UniversalActivity) {
            return;
        }
        DokitIntent dokitIntent = new DokitIntent(CountDownDokitView.class);
        dokitIntent.mode = DokitIntent.MODE_ONCE;
        attach(dokitIntent);
    }

    /**
     * 添加一机多控标识
     *
     * @param activity
     */
    void attachMcDokitView(Activity activity) {
        if (DoKitConstant.INSTANCE.getWS_MODE() == WSMode.UNKNOW) {
            return;
        }

//        DokitAbility.DokitModuleProcessor processor = DoKitConstant.INSTANCE.getModuleProcessor(DoKitModule.MODULE_MC);
//        if (processor == null) {
//            return;
//        }
//
//        if (DoKitConstant.INSTANCE.getWS_MODE() == WSMode.HOST) {
//            Map<String, String> action = new HashMap();
//            action.put("action", "launch_host_view");
//            processor.proceed(action);
//        } else if (DoKitConstant.INSTANCE.getWS_MODE() == WSMode.CLIENT) {
//            Map<String, String> action = new HashMap();
//            action.put("action", "launch_client_view");
//            processor.proceed(action);
//        }

    }
}
