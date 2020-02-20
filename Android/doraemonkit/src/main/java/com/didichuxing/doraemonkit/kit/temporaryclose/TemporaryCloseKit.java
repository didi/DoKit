package com.didichuxing.doraemonkit.kit.temporaryclose;

import android.content.Context;

import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.ui.base.DokitViewManager;

/**
 *
 * @author wanglikun
 * @date 2018/10/26
 * @desc 退出 dokit
 */

public class TemporaryCloseKit extends AbstractKit {


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
        DokitViewManager.getInstance().detachToolPanel();
        DoraemonKit.hide();
    }

    @Override
    public void onAppInit(Context context) {

    }

}
