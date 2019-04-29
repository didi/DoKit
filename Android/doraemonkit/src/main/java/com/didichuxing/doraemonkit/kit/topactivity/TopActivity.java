package com.didichuxing.doraemonkit.kit.topactivity;

import android.content.Context;
import android.content.Intent;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.kit.IKit;
import com.didichuxing.doraemonkit.ui.UniversalActivity;

/**
 * 项目名:    Android
 * 包名       com.didichuxing.doraemonkit.kit.topactivity
 * 文件名:    TopActivity
 * 创建时间:  2019-04-29 on 12:13
 * 描述:     当前栈顶的Activity信息
 *
 * @author 阿钟
 */

public class TopActivity implements IKit {

    @Override
    public int getCategory() {
        return Category.TOOLS;
    }

    @Override
    public int getName() {
        return R.string.dk_kit_top_activity;
    }

    @Override
    public int getIcon() {
        return R.drawable.dk_view_check;
    }

    @Override
    public void onClick(Context context) {
        Intent intent = new Intent(context, UniversalActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(BundleKey.FRAGMENT_INDEX, FragmentIndex.FRAGMENT_TOP_ACTIVITY);
        context.startActivity(intent);
    }

    @Override
    public void onAppInit(Context context) {

    }
}
