package com.didichuxing.doraemonkit.kit.gpsmock;

import android.content.Context;
import android.os.IBinder;
import android.os.IInterface;

import androidx.annotation.Nullable;

import com.didichuxing.doraemonkit.util.LogHelper;
import com.didichuxing.doraemonkit.util.ReflectUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;


/**
 * Created by wanglikun on 2019/4/2
 */
public abstract class BaseServiceHooker implements InvocationHandler {
    private static final String TAG = "BaseServiceHooker";
    protected static final String METHOD_ASINTERFACE = "asInterface";

    /**
     * 本地对象(同进程)或远程的代理对象(跨进程)
     */
    private IInterface mBinderStubProxy;

    public abstract String getServiceName();

    public abstract String getStubName();

    public abstract Map<String, MethodHandler> getMethodHandlers();

    /**
     * @param proxy  就是代理对象，newProxyInstance方法的返回对象
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
        if (mBinderStubProxy == null) {
            return null;
        }
        try {
            //判断要拦截的方法是否实现已注册
            if (getMethodHandlers().containsKey(method.getName()) && getMethodHandlers().get(method.getName()) != null) {
                return getMethodHandlers().get(method.getName()).onInvoke(this.mBinderStubProxy, method, args);
            } else {
                return method.invoke(mBinderStubProxy, args);
            }
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 获得 native Binder  proxy
     *
     * @param binder
     */
    @SuppressWarnings("unchecked")
    void asInterface(IBinder binder) {
        try {
            //IInterface 包含了IBinder的proxy 并实现相应的接口能力
            this.mBinderStubProxy = ReflectUtils.reflect(getStubName()).method("asInterface", binder).get();
            //LogHelper.i(TAG, "service asInterface====>" + this.mBinderStubProxy);
//            IBinder iBinder = this.mBinderStubProxy.asBinder();
//            LogHelper.i(TAG, "iBinder====>" + iBinder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void replaceBinder(Context context, IBinder proxy) throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException;

    public interface MethodHandler {
        /**
         * @param originObject 原始对象
         * @param method       需要被代理的方法
         * @param args         代理方法的参数
         * @return
         * @throws InvocationTargetException
         * @throws IllegalAccessException
         * @throws NoSuchFieldException
         * @throws NoSuchMethodException
         */
        @Nullable
        Object onInvoke(Object originObject, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException;
    }
}