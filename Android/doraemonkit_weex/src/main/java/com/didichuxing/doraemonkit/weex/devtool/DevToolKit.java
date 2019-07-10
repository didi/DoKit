package com.didichuxing.doraemonkit.weex.devtool;

import android.content.Context;
import android.content.Intent;

import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.kit.IKit;
import com.didichuxing.doraemonkit.weex.R;

/**
 * @author haojianglong
 * @date 2019-06-11
 */
public class DevToolKit implements IKit {

    @Override
    public int getCategory() {
        return Category.BIZ;
    }

    @Override
    public int getName() {
        return R.string.dk_dev_tool_name;
    }

    @Override
    public int getIcon() {
        return R.drawable.dk_custom;
    }

    @Override
    public void onClick(Context context) {
        Intent intent = new Intent(context, DevToolActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onAppInit(Context context) {

    }

}
