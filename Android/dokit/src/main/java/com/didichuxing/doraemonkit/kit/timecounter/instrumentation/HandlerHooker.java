package com.didichuxing.doraemonkit.kit.timecounter.instrumentation;

import android.app.Application;
import android.os.Build;
import android.os.Handler;

import com.didichuxing.doraemonkit.reflection.Reflection;
import com.didichuxing.doraemonkit.util.ReflectUtils;


public class HandlerHooker {
    private static final String TAG = "HandlerHooker";
    //是否已经hook成功
    private static boolean isHookSucceed = false;

    public static void doHook(Application app) {
        try {
            if (isHookSucceed()) {
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                //解锁调用系统隐藏api的权限
                Reflection.unseal(app);
            }
            //hook ActivityThread的Instrumentation
            hookInstrumentation();
            isHookSucceed = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    static boolean isHookSucceed() {
        return isHookSucceed;
    }

    /**
     * hook ActivityThread的Instrumentation
     */
    private static void hookInstrumentation() {
        //得到ActivityThread对象
        Object currentActivityThreadObj = ReflectUtils.reflect("android.app.ActivityThread").method("currentActivityThread").get();
        //ActivityThread对象的 mH变量
        Handler handlerObj = ReflectUtils.reflect(currentActivityThreadObj).field("mH").get();
        Handler.Callback handCallbackObj = ReflectUtils.reflect(handlerObj).field("mCallback").get();
        ProxyHandlerCallback proxyMHCallback = new ProxyHandlerCallback(handCallbackObj, handlerObj);
        //替换mCallback 对象
        ReflectUtils.reflect(handlerObj).field("mCallback", proxyMHCallback);

    }

}
