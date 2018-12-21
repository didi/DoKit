package com.didichuxing.doraemonkit.kit.sysinfo;

import android.content.Context;
import android.content.Intent;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.kit.IKit;
import com.didichuxing.doraemonkit.ui.UniversalActivity;

/**
 * 设备、app信息
 * Created by zhangweida on 2018/6/22.
 */

public class SysInfo implements IKit {
    @Override
    public int getCategory() {
        return Category.TOOLS;
    }

    @Override
    public int getName() {
        return R.string.dk_kit_sysinfo;
    }

    @Override
    public int getIcon() {
        return R.drawable.dk_sys_info;
    }

    @Override
    public void onClick(Context context) {
        Intent intent = new Intent(context, UniversalActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(BundleKey.FRAGMENT_INDEX, FragmentIndex.FRAGMENT_SYS_INFO);
        context.startActivity(intent);
    }

    @Override
    public void onAppInit(Context context) {

    }

}
