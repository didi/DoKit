package com.didichuxing.doraemonkit.util.netstate;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author maple on 2019/7/9 16:09.
 * @version v1.0
 * @see 1040441325@qq.com
 */

public class NetworkManager {
    public static final String TAG = "NetworkManager";
    private static volatile NetworkManager instance;

    private Context context;
    private INetwork receiver;
    private HashMap<Object, List<NetworkMethod>> mMap = new HashMap<>();
    private final NetChangeObserver observer = new NetChangeObserver() {
        @Override
        public void onConnect(NetType netType) {
            for (Map.Entry<Object, List<NetworkMethod>> entry : mMap.entrySet()) {
                List<NetworkMethod> tmp = entry.getValue();
                for (NetworkMethod nwMethod : tmp) {
                    if (nwMethod.getType() == NetType.AUTO) {
                        invoke(nwMethod.getMethod(), entry.getKey(), netType);
                    } else if (nwMethod.getType() == netType) {
                        invoke(nwMethod.getMethod(), entry.getKey(), netType);
                    }
                }
            }
        }

        @Override
        public void onDisConnect() {
            for (Map.Entry<Object, List<NetworkMethod>> entry : mMap.entrySet()) {
                List<NetworkMethod> tmp = entry.getValue();
                for (NetworkMethod nwMethod : tmp) {
                    invoke(nwMethod.getMethod(), entry.getKey(), NetType.NONE);
                }
            }
        }
    };

    private void invoke(final Method method, final Object key, final NetType param) {

        Log.i(TAG, "invoke: " + method.getName() + "," + key.getClass().getSimpleName() + "," + param.toString());
        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                try {
                    method.invoke(key, param);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public synchronized static void init(Context context) {
        if (context == null) return;
        instance = new NetworkManager(context);
    }

    private NetworkManager(Context context) {
        this.context = context;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // NetworkCallback
            this.receiver = new NetWorkCallbackImpl();
        } else {
            // 广播,低版本使用广播监听网络变化
            this.receiver = new NetStateReceiver();
        }
        receiver.register(observer, context);


    }


    public static NetworkManager get() {
        if (instance == null) {
            throw new RuntimeException("need init before");
        }
        return instance;
    }

    //不支持类继承注解
    public void register(Object o) {
        if (mMap.containsKey(o)) {
            Log.e(TAG, "register: 重复注册");
            return;
        }
        Class<?> clazz = o.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        ArrayList<NetworkMethod> tmp = new ArrayList<>();
        for (Method method : methods) {
            NetWork anno = method.getAnnotation(NetWork.class);
            if (anno == null) continue;
            if (method.getParameterTypes().length != 1 || !method.getParameterTypes()[0].isAssignableFrom(NetType.class)) {
                throw new RuntimeException(method.getName() + "必须有且仅有参数NetType");
            }
            tmp.add(new NetworkMethod(method, method.getParameterTypes()[0], anno.value()));
        }
        mMap.put(o, tmp);
    }

    public void unRegister(Object o) {
        mMap.remove(o);
    }

    public void unRegisterAll() {
        mMap.clear();
        receiver.unRegister(context);
    }
}
