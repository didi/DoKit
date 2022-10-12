package com.example.androidpowercomsumption.utils.systemservice.hooker;

import android.os.IBinder;
import android.os.IInterface;
import android.util.Log;
import androidx.annotation.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class BluetoothServiceHooker {
    private static final String TAG = "ServiceController";

    public int registerTime = 0;

    public int discoveryTime = 0;

    public int scanTime = 0;

    private ServiceHookCallback sHookCallback = new ServiceHookCallback() {
        @Override
        public void invoke(Method method, Object[] args) {
        }

        @Nullable
        @Override
        public Object intercept(Object receiver, Method method, Object[] args) throws Throwable {
            if ("registerAdapter".equals(method.getName())) {
                Object blueTooth = method.invoke(receiver, args);
                Object proxy = proxyBluetooth(blueTooth);
                return proxy == null ? blueTooth : proxy;
            } else if ("getBluetoothGatt".equals(method.getName())) {
                Object blueToothGatt = method.invoke(receiver, args);
                Object proxy = proxyBluetoothGatt(blueToothGatt);
                return proxy == null ? blueToothGatt : proxy;
            }
            return null;
        }
    };

    public SystemServiceHooker sHookHelper = new SystemServiceHooker("bluetooth_manager", "android.bluetooth.IBluetoothManager", sHookCallback);

    private Object proxyBluetooth(final Object delegate) {
        try {
            Class<?> clazz = Class.forName("android.bluetooth.IBluetooth");
            Class<?>[] interfaces = new Class<?>[]{IBinder.class, IInterface.class, clazz};
            ClassLoader loader = delegate.getClass().getClassLoader();
            InvocationHandler handler = (proxy, method, args) -> {
                if ("startDiscovery".equals(method.getName())) {
                    discoveryTime++;
                    Log.d(TAG, "BluetoothServiceHooker: discoveryTime++");
                }

                return method.invoke(delegate, args);
            };
            return Proxy.newProxyInstance(loader, interfaces, handler);
        } catch (Throwable e) {
            Log.d(TAG, "proxyBluetooth fail");
        }
        return null;
    }

    private Object proxyBluetoothGatt(final Object delegate) {
        try {
            Class<?> clazz = Class.forName("android.bluetooth.IBluetoothGatt");
            Class<?>[] interfaces = new Class<?>[]{IBinder.class, IInterface.class, clazz};
            ClassLoader loader = delegate.getClass().getClassLoader();
            InvocationHandler handler = (proxy, method, args) -> {
                if ("registerScanner".equals(method.getName())) {
                    registerTime++;
                    Log.d(TAG, "BluetoothServiceHooker: registerTime++");
                } else if ("startScan".equals(method.getName()) || "startScanForIntent".equals(method.getName())) {
                    scanTime++;
                    Log.d(TAG, "BluetoothServiceHooker: scanTime++");
                }
                return method.invoke(delegate, args);
            };
            return Proxy.newProxyInstance(loader, interfaces, handler);
        } catch (Throwable e) {
            Log.d(TAG, "proxyBluetoothGatt fail");
        }
        return null;
    }
}
