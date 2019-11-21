package com.didichuxing.doraemonkit.weex.storage;

import android.content.Context;

import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.kit.IKit;
import com.didichuxing.doraemonkit.weex.R;
import com.didichuxing.doraemonkit.weex.common.DKCommonActivity;

/**
 * @author haojianglong
 * @date 2019-06-11
 */
public class StorageKit implements IKit {

    @Override
    public int getCategory() {
        return Category.WEEX;
    }

    @Override
    public int getName() {
        return R.string.dk_storage_cache_name;
    }

    @Override
    public int getIcon() {
        return R.drawable.dk_file_explorer;
    }

    @Override
    public void onClick(Context context) {
        DKCommonActivity.startWith(context, StorageFragment.class);
    }

    @Override
    public void onAppInit(Context context) {

    }

}
