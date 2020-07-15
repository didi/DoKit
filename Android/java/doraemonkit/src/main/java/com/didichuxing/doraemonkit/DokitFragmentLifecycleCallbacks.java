package com.didichuxing.doraemonkit;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.didichuxing.doraemonkit.util.LifecycleListenerUtil;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-31-10:56
 * 描    述：全局的fragment 生命周期回调
 * 修订历史：
 * ================================================
 */
public class DokitFragmentLifecycleCallbacks extends FragmentManager.FragmentLifecycleCallbacks {
    private static final String TAG = "DokitFragmentLifecycleCallbacks";


    @Override
    public void onFragmentAttached(FragmentManager fm, Fragment fragment, Context context) {
        super.onFragmentAttached(fm, fragment, context);
        //LogHelper.d(TAG, "onFragmentAttached: " + fragment);

        for (LifecycleListenerUtil.LifecycleListener listener : LifecycleListenerUtil.LIFECYCLE_LISTENERS) {
            listener.onFragmentAttached(fragment);
        }
    }

    @Override
    public void onFragmentDetached(FragmentManager fm, Fragment fragment) {
        super.onFragmentDetached(fm, fragment);
        //LogHelper.d(TAG, "onFragmentDetached: " + fragment);

        for (LifecycleListenerUtil.LifecycleListener listener : LifecycleListenerUtil.LIFECYCLE_LISTENERS) {
            listener.onFragmentDetached(fragment);
        }
    }
}
