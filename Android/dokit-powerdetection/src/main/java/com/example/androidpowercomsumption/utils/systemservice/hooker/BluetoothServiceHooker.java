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
                Object proxy = proxy(blueTooth, false);
                return proxy == null ? blueTooth : proxy;
            } else if ("getBluetoothGatt".equals(method.getName())) {
                Object blueToothGatt = method.invoke(receiver, args);
                Object proxy = proxy(blueToothGatt, true);
                return proxy == null ? blueToothGatt : proxy;
            }
            return null;
        }
    };

    public SystemServiceHooker sHookHelper = new SystemServiceHooker("bluetooth_manager", "android.bluetooth.IBluetoothManager", sHookCallback);

    private Object proxy(Object delegate, boolean isGatt) {
        try {
            Class<?>[] interfaces;
            if (!isGatt) {
                interfaces = new Class<?>[]{IBinder.class, IInterface.class, Class.forName("android.bluetooth.IBluetooth")};
            } else {
                interfaces = new Class<?>[]{IBinder.class, IInterface.class, Class.forName("android.bluetooth.IBluetoothGatt")};
            }
            ClassLoader loader = delegate.getClass().getClassLoader();
            InvocationHandler handler = (proxy, method, args) -> {
                if (!isGatt)
                    proxyBluetooth(method);
                else
                    proxyBluetoothGatt(method);
                return method.invoke(delegate, args);
            };
            return Proxy.newProxyInstance(loader, interfaces, handler);
        } catch (Throwable e) {
            Log.d(TAG, "proxyBluetooth fail");
        }
        return null;
    }

    private void proxyBluetooth(Method method) {
        if ("startDiscovery".equals(method.getName())) {
            discoveryTime++;
            Log.d(TAG, "BluetoothServiceHooker: discoveryTime++");
        }
    }

    private void proxyBluetoothGatt(Method method) {
        if ("registerScanner".equals(method.getName())) {
            registerTime++;
            Log.d(TAG, "BluetoothServiceHooker: registerTime++");
        } else if ("startScan".equals(method.getName()) || "startScanForIntent".equals(method.getName())) {
            scanTime++;
            Log.d(TAG, "BluetoothServiceHooker: scanTime++");
        }
    }
}

