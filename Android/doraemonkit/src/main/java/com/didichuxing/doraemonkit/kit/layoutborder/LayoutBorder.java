package com.didichuxing.doraemonkit.kit.layoutborder;

import android.content.Context;
import android.content.Intent;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.LayoutBorderConfig;
import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.kit.IKit;
import com.didichuxing.doraemonkit.ui.UniversalActivity;

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
        Intent intent = new Intent(context, UniversalActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(BundleKey.FRAGMENT_INDEX, FragmentIndex.FRAGMENT_LAYOUT_BORDER);
        context.startActivity(intent);
    }

    @Override
    public void onAppInit(Context context) {
        LayoutBorderConfig.setLayoutBorderOpen(context, false);
        LayoutBorderConfig.setLayoutLevelOpen(context, false);
    }
}