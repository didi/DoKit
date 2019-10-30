package com.didichuxing.doraemonkit.kit.layoutborder;

import android.content.Context;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.LayoutBorderConfig;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.kit.IKit;
import com.didichuxing.doraemonkit.ui.base.DokitIntent;
import com.didichuxing.doraemonkit.ui.base.DokitViewManager;

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

        //隐藏当前工具popview
        DokitViewManager.getInstance().detachToolPanel();

        DokitIntent intent = new DokitIntent(LayoutLevelDokitView.class);
        intent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
        DokitViewManager.getInstance().attach(intent);

        LayoutBorderManager.getInstance().start();

        LayoutBorderConfig.setLayoutBorderOpen(true);
        LayoutBorderConfig.setLayoutLevelOpen(true);
    }

    @Override
    public void onAppInit(Context context) {
        LayoutBorderConfig.setLayoutBorderOpen(false);
        LayoutBorderConfig.setLayoutLevelOpen(false);
    }
}