package com.didichuxing.doraemonkit.kit.alignruler;

import android.content.Context;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.AlignRulerConfig;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.ui.base.DokitIntent;
import com.didichuxing.doraemonkit.ui.base.DokitViewManagerProxy;

/**
 * Created by wanglikun on 2018/9/19.
 */

public class AlignRulerKit extends AbstractKit {
    @Override
    public int getCategory() {
        return Category.UI;
    }

    @Override
    public int getName() {
        return R.string.dk_kit_align_ruler;
    }

    @Override
    public int getIcon() {
        return R.drawable.dk_align_ruler;
    }

    @Override
    public void onClick(Context context) {
        DokitViewManagerProxy.getInstance().detachToolPanel();

        DokitIntent pageIntent = new DokitIntent(AlignRulerMarkerDokitView.class);
        pageIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
        DokitViewManagerProxy.getInstance().attach(pageIntent);

        pageIntent = new DokitIntent(AlignRulerLineDokitView.class);
        pageIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
        DokitViewManagerProxy.getInstance().attach(pageIntent);

        pageIntent = new DokitIntent(AlignRulerInfoDokitView.class);
        pageIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
        DokitViewManagerProxy.getInstance().attach(pageIntent);


        AlignRulerConfig.setAlignRulerOpen(context, true);
    }

    @Override
    public void onAppInit(Context context) {
        AlignRulerConfig.setAlignRulerOpen(context, false);
    }
}
