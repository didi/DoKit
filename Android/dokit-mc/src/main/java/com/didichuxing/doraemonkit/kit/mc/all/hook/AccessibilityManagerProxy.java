package com.didichuxing.doraemonkit.kit.mc.all.hook;

import android.view.accessibility.AccessibilityManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * didi Create on 2022/2/22 .
 * <p>
 * Copyright (c) 2022/2/22 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/2/22 10:54 上午
 * @Description 用一句话说明文件功能
 */

public class AccessibilityManagerProxy implements InvocationHandler {

    private AccessibilityManager accessibilityManager;

    public AccessibilityManagerProxy(AccessibilityManager accessibilityManager) {
        this.accessibilityManager = accessibilityManager;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object o= method.invoke(accessibilityManager,args);
        return o;
    }
}
