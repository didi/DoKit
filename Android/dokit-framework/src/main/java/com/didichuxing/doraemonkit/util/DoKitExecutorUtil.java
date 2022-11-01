package com.didichuxing.doraemonkit.util;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by wanglikun on 2018/11/15.
 */

public class DoKitExecutorUtil {
    private static ExecutorService sExecutorService;

    private DoKitExecutorUtil() {
    }

    public static void execute(Runnable r) {
        if (sExecutorService == null) {
            sExecutorService = new ThreadPoolExecutor(1, 5, 60L, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(),
                    new ThreadPoolExecutor.AbortPolicy());

        }
        sExecutorService.execute(r);
    }
}