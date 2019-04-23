package com.didichuxing.doraemonkit.kit.network.aspect;

import org.aspectj.lang.NoAspectBoundException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.net.URLConnection;

@Aspect
public class HttpUrlAspect {
    private static final String TAG = "HttpUrlAspect";
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static  /* synthetic */ HttpUrlAspect ajc$perSingletonInstance = null;

    static {
        try {
            ajc$perSingletonInstance = new HttpUrlAspect();
        } catch (Throwable th) {
            ajc$initFailureCause = th;
        }
    }

    public static HttpUrlAspect aspectOf() {
        if (ajc$perSingletonInstance != null) {
            return ajc$perSingletonInstance;
        }
        throw new NoAspectBoundException("com.didichuxing.doraemonkit.kit.network.aspect.HttpUrlAspect", ajc$initFailureCause);
    }

    public static boolean hasAspect() {
        return ajc$perSingletonInstance != null;
    }


    @Around("call(* java.net.URL.openConnection(..))")
    public URLConnection URLOpenConnection(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        URLConnection connection = (URLConnection) proceedingJoinPoint.proceed();
        return AopUtils.URLOpenConnection(connection);
    }

}