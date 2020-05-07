package com.didichuxing.doraemonkit.aop;


import android.util.Log;

import java.lang.reflect.Method;

/**
 * /**
 * * ================================================
 * * 作    者：maple
 * * 版    本：1.0
 * * 创建日期：2020-04-28
 * * 描    述：用来通过ASM在编译器进行hook,代理线程得start方法,打印startr日志
 * * 修订历史：
 * * ================================================
 */

public class ThreadHook {

    public static void log(Thread t) {
        Log.i("ThreadLog", Thread.currentThread().getName() + "("
                + System.identityHashCode(Thread.currentThread()) + ")开启了新线程" + t.getName() + "(" + +System.identityHashCode(t) + ")");
    }

    public static void start(Object t) {

        if (t instanceof Thread) {
            log((Thread) t);
            ((Thread) t).start();
        } else {//捕获了错误的start函数,调用原逻辑
            Class<?> clazz = t.getClass();
            try {
                Method m = clazz.getMethod("start");
                m.setAccessible(true);
                m.invoke(t);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
