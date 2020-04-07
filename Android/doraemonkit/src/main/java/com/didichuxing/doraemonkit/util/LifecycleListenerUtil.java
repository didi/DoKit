package com.didichuxing.doraemonkit.util;

import android.app.Activity;


import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-18-10:27
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class LifecycleListenerUtil {
    public static List<LifecycleListener> LIFECYCLE_LISTENERS = new ArrayList<>();
    /**
     * 悬浮窗初始化时注册activity以及fragment生命周期回调监听
     *
     * @param listener
     */
    public static void registerListener(LifecycleListenerUtil.LifecycleListener listener) {
        LIFECYCLE_LISTENERS.add(listener);
    }

    /**
     * 悬浮窗关闭时注销监听
     *
     * @param listener
     */
    public static void unRegisterListener(LifecycleListenerUtil.LifecycleListener listener) {
        LIFECYCLE_LISTENERS.remove(listener);
    }

    public interface LifecycleListener {
        void onActivityResumed(Activity activity);

        void onActivityPaused(Activity activity);

        void onFragmentAttached(Fragment f);

        void onFragmentDetached(Fragment f);
    }


}
