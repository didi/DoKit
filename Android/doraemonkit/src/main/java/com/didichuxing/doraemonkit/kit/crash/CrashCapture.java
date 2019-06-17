package com.didichuxing.doraemonkit.kit.crash;

import android.content.Context;
import android.content.Intent;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.CrashCaptureConfig;
import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.kit.IKit;
import com.didichuxing.doraemonkit.ui.UniversalActivity;

/**
 * Created by wanglikun on 2019/6/12
 */
public class CrashCapture implements IKit {
    @Override
    public int getCategory() {
        return Category.TOOLS;
    }

    @Override
    public int getName() {
        return R.string.dk_kit_crash;
    }

    @Override
    public int getIcon() {
        return R.drawable.dk_crash_catch;
    }

    @Override
    public void onClick(Context context) {
        Intent intent = new Intent(context, UniversalActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(BundleKey.FRAGMENT_INDEX, FragmentIndex.FRAGMENT_CRASH);
        context.startActivity(intent);
    }

    @Override
    public void onAppInit(Context context) {
        CrashCaptureManager.getInstance().init(context);
        if (CrashCaptureConfig.isCrashCaptureOpen(context)) {
            CrashCaptureManager.getInstance().start();
        } else {
            CrashCaptureManager.getInstance().stop();
        }
    }
}