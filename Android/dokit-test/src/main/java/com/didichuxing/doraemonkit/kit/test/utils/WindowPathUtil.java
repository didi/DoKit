package com.didichuxing.doraemonkit.kit.test.utils;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewParent;
import android.view.Window;

import com.didichuxing.doraemonkit.kit.core.DoKitFrameLayout;
import com.didichuxing.doraemonkit.kit.test.event.WindowNode;
import com.didichuxing.doraemonkit.test.R;
import com.didichuxing.doraemonkit.util.RandomUtils;
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

    /**
     * 过滤出Window上的Activity 和Dialog
     */
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


    public static List<ViewParent> filterWindowViewRoot(List<ViewParent> viewParents, WindowNode windowNode) {
        List<ViewParent> parents = new ArrayList<>();
        if (viewParents != null) {
            for (ViewParent parent : viewParents) {
                WindowNode node = createWindowNode(parent);
                if (TextUtils.equals(node.getName(), windowNode.getName())
                    && node.getType() == windowNode.getType()
                    && TextUtils.equals(node.getRootViewName(), windowNode.getRootViewName())) {
                    parents.add(parent);
                }
            }
        }
        return parents;
    }


    public static WindowNode createWindowNode(ViewParent parent) {
        WindowNode windowNode;
        View rootView = ReflectUtils.reflect(parent).field("mView").get();
        windowNode = createWindowNode(rootView);
        return windowNode;
    }

    public static WindowNode createWindowNode(View rootView) {
        WindowNode windowNode;
        Object node = rootView.getTag(R.id.dokit_test_windowNode);
        if (node == null) {
            Window window = ReflectUtils.reflect(rootView).field("mWindow").get();
            windowNode = new WindowNode(
                window.getAttributes().getTitle().toString(),
                RandomUtils.random16HexString(),
                window.getAttributes().type,
                rootView.getClass().getName(),
                0
            );
            rootView.setTag(R.id.dokit_test_windowNode, windowNode);
        } else {
            windowNode = (WindowNode) node;
        }
        return windowNode;
    }


    /**
     * 过滤出Window上的Activity 和Dialog
     */
    public static List<ViewParent> filterDoKitViewRoot(List<ViewParent> viewParents) {
        List<ViewParent> parents = new ArrayList<>();
        if (viewParents != null) {
            for (ViewParent parent : viewParents) {
                if (isDoKitViewRoot(parent)) {
                    parents.add(parent);
                }
            }
        }
        return parents;
    }

    /**
     * 找出当前屏幕上可见的视图
     */
    public static List<ViewParent> filterShowViewRoot(List<ViewParent> viewParents) {
        List<ViewParent> parents = new ArrayList<>();
        if (viewParents != null) {
            for (ViewParent parent : viewParents) {
                Boolean mAppVisible = ReflectUtils.reflect(parent).field("mAppVisible").get();
                if (mAppVisible) {
                    parents.add(parent);
                }
            }
        }
        return parents;
    }


    public static boolean isPageViewRoot(ViewParent viewParent) {
        if (viewParent != null) {
            View view = ReflectUtils.reflect(viewParent).field("mView").get();
            if (isClass(view, DECOR_VIEW)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDoKitViewRoot(ViewParent viewParent) {
        if (viewParent != null) {
            View view = ReflectUtils.reflect(viewParent).field("mView").get();
            if (view instanceof DoKitFrameLayout) {
                return true;
            }
        }
        return false;
    }

    public static String getsDoKitViewRootTitle(ViewParent viewParent) {
        if (viewParent != null) {
            View view = ReflectUtils.reflect(viewParent).field("mView").get();
            if (view instanceof DoKitFrameLayout) {
                return ((DoKitFrameLayout) view).getTitle();
            }
        }
        return "";
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
