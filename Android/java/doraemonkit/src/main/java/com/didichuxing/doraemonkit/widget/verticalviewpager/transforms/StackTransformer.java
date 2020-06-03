package com.didichuxing.doraemonkit.widget.verticalviewpager.transforms;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-27-14:56
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class StackTransformer implements ViewPager.PageTransformer {
    @Override
    public void transformPage(View page, float position) {
        page.setTranslationX(page.getWidth() * -position);
        page.setTranslationY(position < 0 ? position * page.getHeight() : 0f);
    }
}
