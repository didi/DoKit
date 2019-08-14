package com.didichuxing.doraemonkit.kit.gpsmock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.IBinder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wanglikun on 2019/4/2
 */
public class ServiceHookManager {
    private static final String CLASS_SERVICE_MANAGER = "android.os.ServiceManager";
    private static final String METHOD_GET_SERVICE = "getService";
    private static final String FIELD_S_CACHE = "sCache";

    private boolean isHookSuccess;

    private List<BaseServiceHooker> mHookers = new ArrayList<>();

    private static class Holder {
        private static ServiceHookManager INSTANCE = new ServiceHookManager();
    }

    public static ServiceHookManager getInstance() {
        return Holder.INSTANCE;
    }

    private ServiceHookManager() {
        init();
    }

    private void init() {
        mHookers.add(new WifiHooker());
        mHookers.add(new LocationHooker());
        mHookers.add(new TelephonyHooker());
    }

    @SuppressLint("PrivateApi")
    @SuppressWarnings("unchecked")
    public void install(Context context) {
        try {
            Class serviceManager = Class.forName(CLASS_SERVICE_MANAGER);
            Method getService = serviceManager.getDeclaredMethod(METHOD_GET_SERVICE, String.class);
            for (BaseServiceHooker hooker : mHookers) {
                IBinder binder = (IBinder) getService.invoke(null, hooker.getServiceName());
                if (binder == null) {
                    return;
                }
                ClassLoader classLoader = binder.getClass().getClassLoader();
                Class[] interfaces = {IBinder.class};
                BinderHookHandler handler = new BinderHookHandler(binder, hooker);
                hooker.setBinder(binder);
                IBinder proxy = (IBinder) Proxy.newProxyInstance(classLoader, interfaces, handler);
                hooker.replaceBinder(context, proxy);
                Field sCache = serviceManager.getDeclaredField(FIELD_S_CACHE);
                sCache.setAccessible(true);
                Map<String, IBinder> cache = (Map<String, IBinder>) sCache.get(null);
                cache.put(hooker.getServiceName(), proxy);
                sCache.setAccessible(false);
            }
            isHookSuccess = true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public boolean isHookSuccess() {
        return isHookSuccess;
    }
}