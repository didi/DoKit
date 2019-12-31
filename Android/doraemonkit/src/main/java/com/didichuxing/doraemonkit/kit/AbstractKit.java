package com.didichuxing.doraemonkit.kit;

import android.content.Context;
import android.content.Intent;

import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.ui.UniversalActivity;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-11-20-15:29
 * 描    述：
 * 修订历史：
 * ================================================
 */
public abstract class AbstractKit implements IKit {

    /**
     * 启动UniversalActivity
     *
     * @param context
     * @param fragmentIndex
     */
    public void startUniversalActivity(Context context, int fragmentIndex) {
        Intent intent = new Intent(context, UniversalActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(BundleKey.FRAGMENT_INDEX, fragmentIndex);
        context.startActivity(intent);
    }
}
