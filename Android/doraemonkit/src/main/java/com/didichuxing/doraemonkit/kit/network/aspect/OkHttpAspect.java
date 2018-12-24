package com.didichuxing.doraemonkit.kit.network.aspect;

import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.kit.network.okhttp.DoraemonInterceptor;
import com.didichuxing.doraemonkit.util.LogHelper;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.NoAspectBoundException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import okhttp3.OkHttpClient.Builder;

@Aspect
public class OkHttpAspect {
    private static final String TAG = "OkHttpAspect";
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static  /* synthetic */ OkHttpAspect ajc$perSingletonInstance = null;

    static {
        try {
            ajc$perSingletonInstance = new OkHttpAspect();
        } catch (Throwable th) {
            ajc$initFailureCause = th;
        }
    }

    public static OkHttpAspect aspectOf() {
        if (ajc$perSingletonInstance != null) {
            return ajc$perSingletonInstance;
        }
        throw new NoAspectBoundException("com.didichuxing.doraemonkit.kit.network.aspect.OkHttpAspect", ajc$initFailureCause);
    }

    public static boolean hasAspect() {
        return ajc$perSingletonInstance != null;
    }

    @Before("call(* okhttp3.OkHttpClient.Builder.build(..))")
    public void addInterceptor(JoinPoint joinPoint) {
        if (NetworkManager.isActive()) {
            LogHelper.i(TAG, "add DoraemonInterceptor success");
            ((Builder) joinPoint.getTarget()).addInterceptor(new DoraemonInterceptor());
        }
    }
}