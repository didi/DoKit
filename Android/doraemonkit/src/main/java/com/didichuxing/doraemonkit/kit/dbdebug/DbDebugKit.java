package com.didichuxing.doraemonkit.kit.dbdebug;

import android.content.Context;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-09-24-17:05
 * 描    述：数据库远程访问入口
 * 修订历史：
 * ================================================
 */
public class DbDebugKit extends AbstractKit {
    @Override
    public int getCategory() {
        return Category.TOOLS;
    }

    @Override
    public int getName() {
        return R.string.dk_tools_dbdebug;
    }

    @Override
    public int getIcon() {
        return R.drawable.dk_db_view;
    }

    @Override
    public void onClick(Context context) {
        startUniversalActivity(context, FragmentIndex.FRAGMENT_DB_DEBUG);

    }

    @Override
    public void onAppInit(Context context) {

    }
}
