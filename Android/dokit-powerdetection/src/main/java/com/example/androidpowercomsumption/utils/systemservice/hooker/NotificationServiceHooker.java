package com.example.androidpowercomsumption.utils.systemservice.hooker;

import android.content.Context;
import android.util.Log;
import androidx.annotation.Nullable;

import java.lang.reflect.Method;

public class NotificationServiceHooker {
    private static final String TAG = "ServiceController";

    public int createChannelTime = 0;

    public int notifyTime = 0;

    private ServiceHookCallback sHookCallback = new ServiceHookCallback() {
        @Override
        public void invoke(Method method, Object[] args) {
            if ("createNotificationChannels".equals(method.getName())) {
                createChannelTime++;
                Log.d(TAG, "NotificationServiceHooker: createChannelTime++");
            } else if ("enqueueNotificationWithTag".equals(method.getName())) {
                notifyTime++;
                Log.d(TAG, "NotificationServiceHooker: notifyTime++;");
            }
        }

        @Nullable
        @Override
        public Object intercept(Object receiver, Method method, Object[] args) throws Throwable {
            return null;
        }
    };

    public SystemServiceHooker sHookHelper = new SystemServiceHooker(Context.NOTIFICATION_SERVICE, "android.app.INotificationManager", sHookCallback);

}
