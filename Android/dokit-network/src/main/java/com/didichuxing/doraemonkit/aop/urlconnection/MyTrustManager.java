package com.didichuxing.doraemonkit.aop.urlconnection;

import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.annotation.Nullable;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

/**
 * didi Create on 2022/5/24 .
 * <p>
 * Copyright (c) 2022/5/24 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/5/24 5:34 下午
 * @Description 用一句话说明文件功能
 */

public class MyTrustManager {


    private static final X509TrustManager trustManager = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    };

    public X509TrustManager getTrustManager() {
        return trustManager;
    }

    public X509TrustManager buildTrustManager() {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new X509TrustManager[]{trustManager}, new SecureRandom());
            SSLSocketFactory ssl = sslContext.getSocketFactory();
            return trustManager(ssl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private X509TrustManager trustManager2(SSLSocketFactory sslSocketFactory) {
        // Attempt to get the trust manager from an OpenJDK socket factory. We attempt this on all
        // platforms in order to support Robolectric, which mixes classes from both Android and the
        // Oracle JDK. Note that we don't support HTTP/2 or other nice features on Robolectric.
        try {
            Class<?> sslContextClass = Class.forName("sun.security.ssl.SSLContextImpl");
            Object context = readFieldOrNull(sslSocketFactory, sslContextClass, "context");
            if (context == null) return null;
            return readFieldOrNull(context, X509TrustManager.class, "trustManager");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private X509TrustManager trustManager(SSLSocketFactory sslSocketFactory) throws Exception {

        Class sslParametersClass = Class.forName("com.android.org.conscrypt.SSLParametersImpl");
        Class sslSocketClass = Class.forName("com.android.org.conscrypt.OpenSSLSocketImpl");

        Object context = readFieldOrNull(sslSocketFactory, sslParametersClass, "sslParameters");
        if (context == null) {
            // If that didn't work, try the Google Play Services SSL provider before giving up. This
            // must be loaded by the SSLSocketFactory's class loader.
            try {
                Class<?> gmsSslParametersClass = Class.forName(
                    "com.google.android.gms.org.conscrypt.SSLParametersImpl", false,
                    sslSocketFactory.getClass().getClassLoader());
                context = readFieldOrNull(sslSocketFactory, gmsSslParametersClass, "sslParameters");
            } catch (ClassNotFoundException e) {
                return trustManager2(sslSocketFactory);
            }
        }

        X509TrustManager x509TrustManager = readFieldOrNull(
            context, X509TrustManager.class, "x509TrustManager");
        if (x509TrustManager != null) return x509TrustManager;

        return readFieldOrNull(context, X509TrustManager.class, "trustManager");
    }

    static @Nullable
    <T> T readFieldOrNull(Object instance, Class<T> fieldType, String fieldName) {
        for (Class<?> c = instance.getClass(); c != Object.class; c = c.getSuperclass()) {
            try {
                Field field = c.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(instance);
                if (!fieldType.isInstance(value)) return null;
                return fieldType.cast(value);
            } catch (NoSuchFieldException ignored) {
            } catch (IllegalAccessException e) {
                throw new AssertionError();
            }
        }

        // Didn't find the field we wanted. As a last gasp attempt, try to find the value on a delegate.
        if (!fieldName.equals("delegate")) {
            Object delegate = readFieldOrNull(instance, Object.class, "delegate");
            if (delegate != null) return readFieldOrNull(delegate, fieldType, fieldName);
        }

        return null;
    }
}
