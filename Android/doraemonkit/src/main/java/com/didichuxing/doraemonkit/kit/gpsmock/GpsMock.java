package com.didichuxing.doraemonkit.kit.gpsmock;

import android.content.Context;
import android.content.Intent;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.GpsMockConfig;
import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.kit.IKit;
import com.didichuxing.doraemonkit.ui.UniversalActivity;

/**
 * Created by wanglikun on 2018/9/20.
 */

public class GpsMock implements IKit {
    @Override
    public int getCategory() {
        return Category.TOOLS;
    }

    @Override
    public int getName() {
        return R.string.dk_kit_gps_mock;
    }

    @Override
    public int getIcon() {
        return  R.drawable.dk_gps_mock;
    }

    @Override
    public void onClick(Context context) {
        Intent intent = new Intent(context, UniversalActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(BundleKey.FRAGMENT_INDEX, FragmentIndex.FRAGMENT_GPS_MOCK);
        context.startActivity(intent);
    }

    @Override
    public void onAppInit(Context context) {
        if (GpsMockConfig.isGPSMockOpen(context)) {
            GpsMockManager.getInstance().startMock();
        }
    }

}