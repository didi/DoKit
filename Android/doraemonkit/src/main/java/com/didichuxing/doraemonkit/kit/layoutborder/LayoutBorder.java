package com.didichuxing.doraemonkit.kit.layoutborder;

import android.content.Context;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.LayoutBorderConfig;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.kit.IKit;
import com.didichuxing.doraemonkit.ui.base.FloatPageManager;
import com.didichuxing.doraemonkit.ui.base.PageIntent;

/**
 * Created by wanglikun on 2019/1/7
 */
public class LayoutBorder implements IKit {
    @Override
    public int getCategory() {
        return Category.UI;
    }

    @Override
    public int getName() {
        return R.string.dk_kit_layout_border;
    }

    @Override
    public int getIcon() {
        return R.drawable.dk_view_border;
    }

    @Override
    public void onClick(Context context) {
        LayoutBorderManager.getInstance().start();
        LayoutBorderConfig.setLayoutBorderOpen(true);
        PageIntent intent = new PageIntent(LayoutLevelFloatPage.class);
        intent.mode = PageIntent.MODE_SINGLE_INSTANCE;
        FloatPageManager.getInstance().add(intent);
        LayoutBorderConfig.setLayoutLevelOpen(true);
    }

    @Override
    public void onAppInit(Context context) {
        LayoutBorderConfig.setLayoutBorderOpen(false);
        LayoutBorderConfig.setLayoutLevelOpen(false);
    }
}