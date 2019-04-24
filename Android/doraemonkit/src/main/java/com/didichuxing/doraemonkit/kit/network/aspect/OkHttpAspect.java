package com.didichuxing.doraemonkit.kit.network.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

import okhttp3.OkHttpClient;

@Aspect
public class OkHttpAspect {
    private static final String TAG = "OkHttpAspect";

    @After("execution(okhttp3.OkHttpClient.Builder.new(..))")
    public void addInterceptor(JoinPoint joinPoint) {
        if (joinPoint.getArgs() != null && joinPoint.getArgs().length > 0 && joinPoint.getArgs()[0] instanceof OkHttpClient) {
            AopUtils.addInterceptor((OkHttpClient.Builder) joinPoint.getTarget());
        }
    }
}