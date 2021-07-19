package com.didichuxing.doraemonkit.widget.verticalviewpager.transforms;

import androidx.viewpager.widget.ViewPager;

import android.view.View;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-27-14:55
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class DefaultTransformer implements ViewPager.PageTransformer {

    @Override
    public void transformPage(View view, float position) {
        float alpha = 0;
        if (0 <= position && position <= 1) {
            alpha = 1 - position;
        } else if (-1 < position && position < 0) {
            alpha = position + 1;
        }
        view.setAlpha(alpha);
        view.setTranslationX(view.getWidth() * -position);
        float yPosition = position * view.getHeight();
        view.setTranslationY(yPosition);
    }
}
