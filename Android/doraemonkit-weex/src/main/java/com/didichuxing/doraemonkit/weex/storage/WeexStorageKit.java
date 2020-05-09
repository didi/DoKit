package com.didichuxing.doraemonkit.weex.storage;

import android.content.Context;

import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.weex.R;
import com.didichuxing.doraemonkit.weex.common.DKCommonActivity;

/**
 * @author haojianglong
 * @date 2019-06-11
 */
public class WeexStorageKit extends AbstractKit {



    @Override
    public int getName() {
        return R.string.dk_storage_cache_name;
    }

    @Override
    public int getIcon() {
        return R.mipmap.dk_file_explorer;
    }

    @Override
    public void onClick(Context context) {
        DKCommonActivity.startWith(context, StorageFragment.class);
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
        return "dokit_sdk_weex_ck_storage";
    }


}
