package com.didichuxing.doraemonkit.kit.timecounter.instrumentation;

import android.app.Application;
import android.os.Handler;

import com.didichuxing.doraemonkit.reflection.Reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class HandlerHooker {
    private static final String TAG = "HandlerHooker";
    //是否已经hook成功
    private static boolean isHookSucceed = false;

    public static void doHook(Application app) {
        try {
            if (isHookSucceed()) {
                return;
            }
            //解锁调用系统隐藏api的权限
            Reflection.unseal(app);
            hookInstrumentation();
            isHookSucceed = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    static boolean isHookSucceed() {
        return isHookSucceed;
    }

    private static void hookInstrumentation() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {

        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
        boolean acc = currentActivityThreadMethod.isAccessible();
        if (!acc) {
            currentActivityThreadMethod.setAccessible(true);
        }
        Object currentActivityThreadObj = currentActivityThreadMethod.invoke(null);
        if (!acc) {
            currentActivityThreadMethod.setAccessible(acc);
        }
        Field handlerField = activityThreadClass.getDeclaredField("mH");
        acc = handlerField.isAccessible();
        if (!acc) {
            handlerField.setAccessible(true);
        }

        Handler handlerObj = (Handler) handlerField.get(currentActivityThreadObj);
        if (!acc) {
            handlerField.setAccessible(acc);
        }

        Field handlerCallbackField = Handler.class.getDeclaredField("mCallback");
        acc = handlerCallbackField.isAccessible();
        if (!acc) {
            handlerCallbackField.setAccessible(true);
        }
        Handler.Callback oldCallbackObj = (Handler.Callback) handlerCallbackField.get(handlerObj);
        //自定义handlerCallback
        ProxyHandlerCallback proxyMHCallback = new ProxyHandlerCallback(oldCallbackObj, handlerObj);
        //将自定义callback注入到activityThread的mH对象中 后期回调会走ProxyHandlerCallback
        handlerCallbackField.set(handlerObj, proxyMHCallback);
        if (!acc) {
            handlerCallbackField.setAccessible(acc);
        }
    }

}