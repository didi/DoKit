package com.didichuxing.doraemonkit.kit.network.aspect;

import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.RequestHandlingOutputStream;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.URLConnectionManager;
import com.didichuxing.doraemonkit.util.LogHelper;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.NoAspectBoundException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
public class HttpUrlAspect {
    private static final String TAG = "HttpUrlAspect";
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static  /* synthetic */ HttpUrlAspect ajc$perSingletonInstance = null;
    public ConcurrentHashMap<HttpURLConnection, URLConnectionManager> mManagerMap = new ConcurrentHashMap();

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

    @Before("call(* java.net.HttpURLConnection.connect(..))")
    public void fetchRequestInfo(JoinPoint joinPoint) {
        if (NetworkManager.isActive()) {
            LogHelper.i(TAG, "hook before call connect");
            HttpURLConnection connection = (HttpURLConnection) joinPoint.getTarget();
            if (this.mManagerMap.get(connection) == null) {
                preConnect(connection);
            }
        }
    }

    private URLConnectionManager preConnect(HttpURLConnection connection) {
        URLConnectionManager manager = new URLConnectionManager();
        manager.preConnect(connection);
        this.mManagerMap.put(connection, manager);
        return manager;
    }

    @Around("call(* java.net.HttpURLConnection.getOutputStream(..))")
    public OutputStream fetchRequestBody(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        if (!NetworkManager.isActive()) {
            return (OutputStream) proceedingJoinPoint.proceed();
        }
        LogHelper.i(TAG, "hook method getOutputStream");
        HttpURLConnection connection = (HttpURLConnection) proceedingJoinPoint.getTarget();
        if (this.mManagerMap.get(connection) != null) {
            OutputStream outputStream = (OutputStream) proceedingJoinPoint.proceed();
            URLConnectionManager manager = this.mManagerMap.get(connection);
            if (manager != null) {
                return new RequestHandlingOutputStream(outputStream, manager);
            }
            return outputStream;
        }
        return new RequestHandlingOutputStream((OutputStream) proceedingJoinPoint.proceed(), preConnect(connection));
    }

    @Around("call(* java.net.HttpURLConnection.getResponseCode(..))")
    public int fetchResponseInfo(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        if (!NetworkManager.isActive()) {
            return ((Integer) proceedingJoinPoint.proceed()).intValue();
        }
        LogHelper.i(TAG, "hook after call getResponseCode");
        HttpURLConnection connection = (HttpURLConnection) proceedingJoinPoint.getTarget();
        if (this.mManagerMap.get(connection) == null) {
            preConnect(connection);
        }
        URLConnectionManager manager = this.mManagerMap.get(connection);
        manager.fetchRequestBody();
        int code = ((Integer) proceedingJoinPoint.proceed()).intValue();
        if (manager != null) {
            manager.postConnect(code);
        }

        return code;
    }

    @Around("call(* java.net.HttpURLConnection.getInputStream(..))")
    public InputStream fetchResponseBody(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        if (!NetworkManager.isActive()) {
            return (InputStream) proceedingJoinPoint.proceed();
        }
        LogHelper.i(TAG, "hook method getInputStream");
        InputStream inputStream = (InputStream) proceedingJoinPoint.proceed();
        HttpURLConnection connection = (HttpURLConnection) proceedingJoinPoint.getTarget();
        if (this.mManagerMap.get(connection) == null) {
            return inputStream;
        }
        return this.mManagerMap.remove(connection).interpretResponseStream(inputStream);
    }
}