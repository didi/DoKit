package com.didichuxing.doraemonkit.kit.largepicture;

import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.util.ToastUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.kit.IKit;
import com.didichuxing.doraemonkit.ui.UniversalActivity;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-09-24-17:05
 * 描    述：网络大图检测功能入口
 * 修订历史：
 * ================================================
 */
public class LargePictureKit implements IKit {
    @Override
    public int getCategory() {
        return Category.PERFORMANCE;
    }

    @Override
    public int getName() {
        return R.string.dk_frameinfo_big_img;
    }

    @Override
    public int getIcon() {
        return R.drawable.dk_performance_large_picture;
    }

    @Override
    public void onClick(Context context) {
        //ToastUtils.showShort("大图检测");
        Intent intent = new Intent(context, UniversalActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(BundleKey.FRAGMENT_INDEX, FragmentIndex.FRAGMENT_LARGE_PICTURE);
        context.startActivity(intent);
    }

    @Override
    public void onAppInit(Context context) {

    }
}
