package com.didichuxing.doraemonkit.kit.test.utils;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewParent;

import com.didichuxing.doraemonkit.util.ReflectUtils;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * didi Create on 2022/1/14
 * <p>
 * Copyright (c) 2022/1/14 by didiglobal.com
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/1/14 3:38 下午
 * @Description 用于查询定位页面位置
 */

public class WindowPathUtil {

    private static final String DECOR_VIEW = "com.android.internal.policy.DecorView";

    /**
     * 查找Window上页面根视图
     * 备注：去除Toast类型，Toast会干扰页面及页面元素查找
     *
     * @param viewParents viewRootIml 列表
     * @param index       页面位置
     * @return viewRootIml 被查找的对象
     */
    @Nullable
    public static ViewParent findViewRoot(List<ViewParent> viewParents, int index) {
        if (viewParents != null) {
            int size = viewParents.size();
            int indexOff = 0;
            for (int i = 0; i < size; i++) {
                ViewParent tmp = viewParents.get(i);
                View view = ReflectUtils.reflect(tmp).field("mView").get();
                if (isClass(view, DECOR_VIEW)) {
                    if (indexOff == index) {
                        return tmp;
                    }
                    indexOff++;
                }
            }
        }
        return null;
    }

    public static List<ViewParent> filterViewRoot(List<ViewParent> viewParents) {
        List<ViewParent> parents = new ArrayList<>();
        if (viewParents != null) {
            for (ViewParent parent : viewParents) {
                if (isPageViewRoot(parent)) {
                    parents.add(parent);
                }
            }
        }
        return parents;
    }

    private static boolean isPageViewRoot(ViewParent viewParent) {
        if (viewParent != null) {
            View view = ReflectUtils.reflect(viewParent).field("mView").get();
            if (isClass(view, DECOR_VIEW)) {
                return true;
            }
        }
        return false;
    }


    private static boolean isClass(Object o, String name) {
        if (o != null) {
            if (TextUtils.equals(o.getClass().getName(), name)) {
                return true;
            }
        }
        return false;
    }

}
