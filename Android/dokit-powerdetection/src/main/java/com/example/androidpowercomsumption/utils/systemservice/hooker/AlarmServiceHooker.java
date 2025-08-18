package com.example.androidpowercomsumption.utils.systemservice.hooker;

import android.content.Context;
import android.util.Log;
import androidx.annotation.Nullable;

import java.lang.reflect.Method;

public class AlarmServiceHooker {
    private static final String TAG = "ServiceController";

    public int setTime = 0;


    private ServiceHookCallback sHookCallback = new ServiceHookCallback() {
        @Override
        public void invoke(Method method, Object[] args) {
            if (method.getName().equals("set")
                    || method.getName().equals("setRepeating") || method.getName().equals("setInexactRepeating")) {
                setTime++;
                Log.d(TAG, "AlarmServiceHooker:setTime++");
            }
        }

        @Nullable
        @Override
        public Object intercept(Object receiver, Method method, Object[] args) throws Throwable {
            return null;
        }
    };

    public SystemServiceHooker sHookHelper = new SystemServiceHooker(Context.ALARM_SERVICE, "android.app.IAlarmManager", sHookCallback);
}
