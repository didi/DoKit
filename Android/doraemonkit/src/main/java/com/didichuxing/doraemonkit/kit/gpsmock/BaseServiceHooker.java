package com.didichuxing.doraemonkit.kit.gpsmock;

import android.content.Context;
import android.os.IBinder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by wanglikun on 2019/4/2
 */
public abstract class BaseServiceHooker implements InvocationHandler {
    protected static final String METHOD_ASINTERFACE = "asInterface";

    private Object mOriginService;

    public abstract String getServiceName();

    public abstract String getStubName();

    public abstract Map<String, MethodHandler> getMethodHandlers();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException {
        if (getMethodHandlers().containsKey(method.getName())) {
            return getMethodHandlers().get(method.getName()).onInvoke(this.mOriginService, proxy, method, args);
        } else {
            return method.invoke(this.mOriginService, args);
        }
    }

    @SuppressWarnings("unchecked")
    public void setBinder(IBinder binder) {
        try {
            Class stub = Class.forName(getStubName());
            Method asInterface = stub.getDeclaredMethod(METHOD_ASINTERFACE, IBinder.class);
            this.mOriginService = asInterface.invoke(null, binder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void replaceBinder(Context context, IBinder proxy) throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException;

    public interface MethodHandler {
        Object onInvoke(Object originService, Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException;
    }
}