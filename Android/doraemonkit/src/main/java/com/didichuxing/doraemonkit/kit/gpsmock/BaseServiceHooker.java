package com.didichuxing.doraemonkit.kit.gpsmock;

import android.content.Context;
import android.os.IBinder;

import android.support.annotation.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;


/**
 * Created by wanglikun on 2019/4/2
 */
public abstract class BaseServiceHooker implements InvocationHandler {
    protected static final String METHOD_ASINTERFACE = "asInterface";
    /**
     * 系统的真实对象
     */
    private Object mOriginService;

    public abstract String getServiceName();

    public abstract String getStubName();

    public abstract Map<String, MethodHandler> getMethodHandlers();

    /**
     * @param proxy  我们自己包装的代理对象
     * @param method 指代的是我们所要调用真实对象的某个方法的Method对象
     * @param args   指代的是调用真实对象某个方法时接受的参数
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     * @throws NoSuchMethodException
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException {
        if (mOriginService == null && proxy == null) {
            return null;
        }
        if (getMethodHandlers().containsKey(method.getName()) && getMethodHandlers().get(method.getName()) != null) {
            return getMethodHandlers().get(method.getName()).onInvoke(this.mOriginService, proxy, method, args);
        } else {
            return method.invoke(mOriginService, args);
        }
    }

    @SuppressWarnings("unchecked")
    void setBinder(IBinder binder) {
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
        /**
         * @param originObject 原始对象
         * @param proxyObject  生成的代理对象
         * @param method       需要被代理的方法
         * @param args         代理方法的参数
         * @return
         * @throws InvocationTargetException
         * @throws IllegalAccessException
         * @throws NoSuchFieldException
         * @throws NoSuchMethodException
         */
        @Nullable
        Object onInvoke(Object originObject, Object proxyObject, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException;
    }
}