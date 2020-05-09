package com.didichuxing.doraemonkit.kit.leakcanary;

import android.content.Context;
import android.content.Intent;

import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;
import com.squareup.leakcanary.R;
import com.squareup.leakcanary.internal.DisplayLeakActivity;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-09-24-17:05
 * 描    述：内存泄漏leakCanary功能入口
 * 修订历史：
 * ================================================
 */
public class LeakCanaryKit extends AbstractKit {

    @Override
    public int getName() {
        return R.string.dk_frameinfo_leakcanary;
    }

    @Override
    public int getIcon() {
        return R.mipmap.leak_canary_icon;
    }

    @Override
    public void onClick(Context context) {
        Intent intent = new Intent(context, DisplayLeakActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void onAppInit(Context context) {

    }

    @Override
    public boolean isInnerKit() {
        return true;
    }

    @Override
    public String innerKitId() {
        return "dokit_sdk_performance_ck_leak";
    }
}
