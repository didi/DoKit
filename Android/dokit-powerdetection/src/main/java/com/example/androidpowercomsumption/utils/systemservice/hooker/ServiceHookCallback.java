package com.example.androidpowercomsumption.utils.systemservice.hooker;

import androidx.annotation.Nullable;

import java.lang.reflect.Method;

public interface ServiceHookCallback {
    void invoke(Method method, Object[] args);

    @Nullable
    Object intercept(Object receiver, Method method, Object[] args) throws Throwable;
}
