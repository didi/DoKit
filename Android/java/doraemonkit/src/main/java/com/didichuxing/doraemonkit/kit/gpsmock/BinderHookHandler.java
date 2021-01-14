package com.didichuxing.doraemonkit.kit.gpsmock;

import android.annotation.SuppressLint;
import android.os.IBinder;
import android.os.IInterface;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by wanglikun on 2018/12/18.
 */

public class BinderHookHandler implements InvocationHandler{
    private static final String TAG = "BinderHookHandler";
    private IBinder mOriginService;
    private BaseServiceHooker mHooker;

    @SuppressWarnings("unchecked")
    public BinderHookHandler(IBinder binder, BaseServiceHooker hooker) {
        this.mOriginService = binder;
        this.mHooker = hooker;
    }

    @Override
    @SuppressLint("PrivateApi")
    public Object invoke(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
        switch (method.getName()) {
            case "queryLocalInterface":
                Class iManager;
                try {
                    iManager = Class.forName(String.valueOf(args[0]));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return method.invoke(mOriginService, args);
                }
                ClassLoader classLoader = mOriginService.getClass().getClassLoader();
                Class[] interfaces = new Class[]{IInterface.class, IBinder.class, iManager};
                return Proxy.newProxyInstance(classLoader, interfaces, mHooker);
            default:
                return method.invoke(mOriginService, args);
        }
    }
}