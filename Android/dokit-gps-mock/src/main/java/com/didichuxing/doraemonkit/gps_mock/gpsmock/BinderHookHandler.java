package com.didichuxing.doraemonkit.gps_mock.gpsmock;

import android.annotation.SuppressLint;
import android.os.IBinder;
import android.os.IInterface;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by wanglikun on 2018/12/18.
 * https://zhuanlan.zhihu.com/p/60805342
 * 嵌套动态代理
 */
public class BinderHookHandler implements InvocationHandler {
    private static final String TAG = "BinderHookHandler";
    final private IBinder mBinderProxy;
    final private BaseServiceHooker mHooker;

    public BinderHookHandler(IBinder binder, BaseServiceHooker hooker) {
        this.mBinderProxy = binder;
        this.mHooker = hooker;
    }

    @Override
    @SuppressLint("PrivateApi")
    public Object invoke(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {

        // 这里直接返回真正被Hook掉的Service接口
        // 这里的 queryLocalInterface 就不是原本的意思了
        // 我们肯定不会真的返回一个本地接口, 因为我们接管了 asInterface方法的作用
        // 因此必须是一个完整的 asInterface 过的 IInterface对象, 既要处理本地对象,也要处理代理对象
        // 这只是一个Hook点而已, 它原始的含义已经被我们重定义了; 因为我们会永远确保这个方法不返回null
        // 让 asInterface 永远走到if语句的else分支里面
        if ("queryLocalInterface".equals(method.getName())) {
            try {
                Class iinterface = Class.forName(String.valueOf(args[0]));
                //LogHelper.i(TAG, "BinderHookHandler==iinterface==>" + iinterface);
                ClassLoader classLoader = mBinderProxy.getClass().getClassLoader();
                // asInterface 的时候会检测是否是特定类型的接口然后进行强制转换
                // 因此这里的动态代理生成的类型信息的类型必须是正确的
                Class[] interfaces = new Class[]{IInterface.class, IBinder.class, iinterface};
                //返回被动态代理后的IBinder对象
                return Proxy.newProxyInstance(classLoader, interfaces, mHooker);
            } catch (Exception e) {
                //e.printStackTrace();
                return method.invoke(mBinderProxy, args);
            }
        }
        return method.invoke(mBinderProxy, args);
    }
}
