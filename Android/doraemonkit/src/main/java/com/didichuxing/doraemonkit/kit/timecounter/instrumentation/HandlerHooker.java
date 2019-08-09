package com.didichuxing.doraemonkit.kit.timecounter.instrumentation;

import android.app.Application;
import android.os.Handler;

import com.didichuxing.doraemonkit.util.LogHelper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import me.weishu.reflection.Reflection;

import static com.didichuxing.doraemonkit.BuildConfig.DEBUG;

public class HandlerHooker {
    private static final String TAG = "HandlerHooker";
    //是否已经hook成功
    private static boolean isHookSucceed = false;

    public static void doHook(Application app) {
        try {
            if (isHookSucceed()) {
                return;
            }
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
        Class<?> c = Class.forName("android.app.ActivityThread");
        Method currentActivityThread = c.getDeclaredMethod("currentActivityThread");
        boolean acc = currentActivityThread.isAccessible();
        if (!acc) {
            currentActivityThread.setAccessible(true);
        }
        Object o = currentActivityThread.invoke(null);
        if (!acc) {
            currentActivityThread.setAccessible(acc);
        }
        Field f = c.getDeclaredField("mH");
        acc = f.isAccessible();
        if (!acc) {
            f.setAccessible(true);
        }

        Handler handler = (Handler) f.get(o);
        if (!acc) {
            f.setAccessible(acc);
        }

        f = Handler.class.getDeclaredField("mCallback");
        acc = f.isAccessible();
        if (!acc) {
            f.setAccessible(true);
        }
        Handler.Callback oldCallback = (Handler.Callback) f.get(handler);
        ProxyHandlerCallback proxyMHCallback = new ProxyHandlerCallback(oldCallback,handler);
        f.set(handler, proxyMHCallback);
        if (!acc) {
            f.setAccessible(acc);
        }
    }

}