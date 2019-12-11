package com.didichuxing.doraemonkit.kit.logInfo;

import android.content.Context;
import android.content.Intent;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.LogInfoConfig;
import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.kit.IKit;
import com.didichuxing.doraemonkit.ui.UniversalActivity;
import com.didichuxing.doraemonkit.ui.base.DokitIntent;
import com.didichuxing.doraemonkit.ui.base.DokitViewManager;

/**
 * Created by wanglikun on 2018/10/9.
 */

public class LogInfo implements IKit {

    @Override
    public int getCategory() {
        return Category.TOOLS;
    }

    @Override
    public int getName() {
        return R.string.dk_kit_log_info;
    }

    @Override
    public int getIcon() {
        return R.drawable.dk_log_info;
    }

    @Override
    public void onClick(Context context) {
        Intent intent = new Intent(context, UniversalActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(BundleKey.FRAGMENT_INDEX, FragmentIndex.FRAGMENT_LOG_INFO_SETTING);
        context.startActivity(intent);
    }

    @Override
    public void onAppInit(Context context) {

    }
    public void onActivityInit(Context context){
        if(LogInfoConfig.isLogInfoOpen(context)){
            DokitIntent intent = new DokitIntent(LogInfoDokitView.class);
            intent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
            DokitViewManager.getInstance().attach(intent);
        }
    }

}