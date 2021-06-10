package com.didichuxing.doraemonkit.kit.gpsmock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.RequiresApi;

import com.didichuxing.doraemonkit.util.LogHelper;
import com.didichuxing.doraemonkit.util.ReflectUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Created by wanglikun on 2019/4/2
 */
public class ServiceHookManager {
    private static final String TAG = "ServiceHookManager";
    //https://www.androidos.net.cn/android/9.0.0_r8/xref/frameworks/base/core/java/android/os/ServiceManager.java
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
        //mHookers.add(new WifiHooker());
        mHookers.add(new LocationHooker());
        //mHookers.add(new TelephonyHooker());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("PrivateApi")
    @SuppressWarnings("unchecked")
    public void install(Context context) {
        try {
            ReflectUtils serviceManager = ReflectUtils.reflect("android.os.ServiceManager");
            for (BaseServiceHooker hooker : mHookers) {
                //BinderProxy ：反射得到具体的 native IBinder shadow
                IBinder nativeBinderProxy = serviceManager.method("getService", hooker.getServiceName()).get();
                LogHelper.i(TAG, "service in ServiceManager====>" + nativeBinderProxy);
                if (nativeBinderProxy == null) {
                    continue;
                }
                //转化为 native Binder shadow 的 proxy
                hooker.asInterface(nativeBinderProxy);

                ClassLoader classLoader = nativeBinderProxy.getClass().getClassLoader();
                Class[] iBinders = {IBinder.class};

                //目的是为了为IBinder.queryLocalInterface每次返回都不为null 而是返回我们自定义的Binder对象
                /* loader: 用哪个类加载器去加载代理对象
                 *
                 * interfaces:动态代理类需要实现的接口 指定newProxyInstance()方法返回的对象要实现哪些接口
                 *
                 * h:动态代理方法在执行时，会调用h里面的invoke方法去执行
                 */
                BinderHookHandler handler = new BinderHookHandler(nativeBinderProxy, hooker);
                IBinder proxy = (IBinder) Proxy.newProxyInstance(classLoader, iBinders, handler);
                LogHelper.i(TAG, "proxy====>" + proxy.getClass().getSimpleName() + "  mNativeBinderProxy====>" + nativeBinderProxy.toString());
                hooker.replaceBinder(context, proxy);
                //替换成自己的代理对象
                Map<String, IBinder> cache = serviceManager.field("sCache").get();
                cache.forEach(new BiConsumer<String, IBinder>() {
                    @Override
                    public void accept(String name, IBinder iBinder) {
                        LogHelper.i(TAG, "name====>" + name + "   iBinder===>" + iBinder);
                    }
                });

                cache.put(hooker.getServiceName(), proxy);
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