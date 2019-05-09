package com.didichuxing.doraemonkit;

import android.app.Application;

import com.didichuxing.doraemonkit.kit.timecounter.TimeCounterManager;
import com.didichuxing.doraemonkit.util.LogHelper;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * 通用的hooker
 */
@Aspect
public class DoraemonHooker {
    public static final String TAG = "DoraemonHooker";

    @Around("execution(* android.app.Application.onCreate(..))")
    public void install(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        LogHelper.d(TAG, "hook application.onCreate start");
        TimeCounterManager.get().onAppCreateStart();
        proceedingJoinPoint.proceed();
        Application app = (Application) proceedingJoinPoint.getTarget();
        DoraemonKit.install(app);
        TimeCounterManager.get().onAppCreateEnd();
        LogHelper.d(TAG, "hook application.onCreate end");
    }
}