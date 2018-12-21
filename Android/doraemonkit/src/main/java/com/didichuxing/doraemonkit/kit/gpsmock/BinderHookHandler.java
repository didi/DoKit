package com.didichuxing.doraemonkit.kit.gpsmock;

import android.annotation.SuppressLint;
import android.os.IBinder;
import android.os.IInterface;

import com.didichuxing.doraemonkit.util.LogHelper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by wanglikun on 2018/12/18.
 */

public class BinderHookHandler implements InvocationHandler{
    private static final String TAG = "BinderHookHandler";
    private IBinder mOriginService;

    private Class iLocationManager;

    @SuppressLint("PrivateApi")
    @SuppressWarnings("unchecked")
    public BinderHookHandler(IBinder binder) {
        this.mOriginService = binder;
        try {
            this.iLocationManager = Class.forName("android.location.ILocationManager");
        } catch (ClassNotFoundException e) {
            LogHelper.e(TAG, e.toString());
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        switch (method.getName()) {
            case "queryLocalInterface":
                ClassLoader classLoader = mOriginService.getClass().getClassLoader();
                Class[] interfaces = new Class[]{IInterface.class, IBinder.class, iLocationManager};
                LocationHookHandler handler = new LocationHookHandler(this.mOriginService);
                return Proxy.newProxyInstance(classLoader, interfaces, handler);
            default:
                return method.invoke(mOriginService, args);
        }
    }
}