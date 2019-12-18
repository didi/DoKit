package com.didichuxing.doraemonkit.kit.version;

import android.content.Context;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;

/**
 * Created by jintai on 2018/10/26.
 */

public class DokitVersionKit extends AbstractKit {
    @Override
    public int getCategory() {
        return Category.VERSION;
    }

    @Override
    public int getName() {
        return R.string.dk_kit_temporary_close;
    }

    @Override
    public int getIcon() {
        return R.drawable.dk_temporary_close;
    }

    @Override
    public void onClick(Context context) {

    }

    @Override
    public void onAppInit(Context context) {

    }

}
