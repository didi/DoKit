package com.didichuxing.doraemonkit;

import android.app.Application;

import com.didichuxing.doraemonkit.util.LogHelper;

import org.aspectj.lang.NoAspectBoundException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * 通用的hooker
 */
@Aspect
public class DoraemonHooker {
    public static final String TAG = "DoraemonHooker";
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static  /* synthetic */ DoraemonHooker ajc$perSingletonInstance = null;

    static {
        try {
            ajc$perSingletonInstance = new DoraemonHooker();
        } catch (Throwable th) {
            ajc$initFailureCause = th;
        }
    }

    public static DoraemonHooker aspectOf() {
        if (ajc$perSingletonInstance != null) {
            return ajc$perSingletonInstance;
        }
        throw new NoAspectBoundException("com.didichuxing.doraemonkit.DoraemonHooker", ajc$initFailureCause);
    }

    public static boolean hasAspect() {
        return ajc$perSingletonInstance != null;
    }

    @Around("execution(* android.app.Application.onCreate(..))")
    public void install(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        LogHelper.d(TAG, "hook application.onCreate start");
        proceedingJoinPoint.proceed();
        Application app = (Application) proceedingJoinPoint.getTarget();
        DoraemonKit.install(app, true);
        LogHelper.d(TAG, "hook application.onCreate end");
    }
}