package com.didichuxing.doraemonkit.kit.viewcheck;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.didichuxing.doraemonkit.constant.BroadcastAction;
import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.util.LogHelper;

/**
 * Created by wanglikun on 2018/11/20.
 */

public class DebugAccessibilityService extends AccessibilityService {
    private static final String TAG = "DebugAccessibilityService";

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    private boolean isActivityEvent(AccessibilityEvent event) {
        ComponentName activityComponentName = new ComponentName(
                event.getPackageName().toString(),
                event.getClassName().toString());
        try {
            ActivityInfo info = getPackageManager().getActivityInfo(activityComponentName, 0);
            if (info != null) {
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            LogHelper.e(TAG, e.toString());
            return false;
        }
        return true;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        CharSequence pkgName = event.getPackageName();
        if (pkgName == null) {
            return;
        }
        if (!pkgName.equals(getPackageName())) {
            return;
        }
        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return;
        }
        if (!isActivityEvent(event)) {
            return;
        }
        AccessibilityNodeInfo info = event.getSource();
        if (info == null) {
            return;
        }
        Intent intent = new Intent(BroadcastAction.ACTION_ACCESSIBILITY_UPDATE);
        intent.putExtra(BundleKey.ACCESSIBILITY_DATA, info);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    @Override
    public void onInterrupt() {
        LogHelper.d(TAG, "onInterrupt");
    }
}