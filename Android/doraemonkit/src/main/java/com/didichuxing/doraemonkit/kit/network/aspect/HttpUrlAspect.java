package com.didichuxing.doraemonkit.kit.network.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.net.URLConnection;

@Aspect
public class HttpUrlAspect {
    private static final String TAG = "HttpUrlAspect";

    @Around("call(* java.net.URL.openConnection(..))")
    public URLConnection URLOpenConnection(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        URLConnection connection = (URLConnection) proceedingJoinPoint.proceed();
        return AopUtils.URLOpenConnection(connection);
    }

}