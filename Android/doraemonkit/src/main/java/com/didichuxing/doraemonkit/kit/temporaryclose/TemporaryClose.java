package com.didichuxing.doraemonkit.kit.temporaryclose;

import android.content.Context;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.kit.IKit;
import com.didichuxing.doraemonkit.ui.base.FloatPageManager;

/**
 * Created by wanglikun on 2018/10/26.
 */

public class TemporaryClose implements IKit {
    @Override
    public int getCategory() {
        return Category.CLOSE;
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
        FloatPageManager.getInstance().removeAll();
    }

    @Override
    public void onAppInit(Context context) {

    }

}
