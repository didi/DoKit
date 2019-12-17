package com.didichuxing.doraemonkit.kit.mode;

import android.content.Context;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-09-19-11:24
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class FloatModeKit extends AbstractKit {
    @Override
    public int getCategory() {
        return Category.FLOAT_MODE;
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
