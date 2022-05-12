package com.didichuxing.doraemonkit.kit.test.utils;

import android.content.Context;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityManager;

import com.didichuxing.doraemonkit.util.ReflectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/14-20:52
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ReflectHookUtil {
    public static final String TAG = "McUtil";

    /**
     * WindowManagerGlobal 单例
     */
    private static Object mWmgInstnace = null;

    /**
     * AccessibilityManagerService 单例
     */
    public static Object mAccessibilityManagerService;

    /**
     * 从WindowManagerGlobal中获取ViewParent的实例为ViewRootImpl对象
     *
     * @return
     */
    public static List<ViewParent> getRootViewsFromWindowManageGlobal() {
        try {
            if (mWmgInstnace == null) {
                Class wmgClass = Class.forName("android.view.WindowManagerGlobal");
                mWmgInstnace = wmgClass.getMethod("getInstance").invoke(null, (Object[]) null);
                return ReflectUtils.reflect(mWmgInstnace).field("mRoots").get();
            } else {
                return ReflectUtils.reflect(mWmgInstnace).field("mRoots").get();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * hook 相关方法
     */
    public static void hookAccessibilityManager(Context context, boolean enable) {
        try {
            Class<AccessibilityManager> accessibilityManagerClass = ReflectUtils.reflect(AccessibilityManager.class).get();
            AccessibilityManager instance = (AccessibilityManager) accessibilityManagerClass.getMethod("getInstance", Context.class).invoke(null, context);
            //先重置状态
            ReflectUtils.reflect(instance).field("mIsEnabled", enable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
