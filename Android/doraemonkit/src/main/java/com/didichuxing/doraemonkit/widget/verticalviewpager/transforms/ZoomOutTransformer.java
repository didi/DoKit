package com.didichuxing.doraemonkit.widget.verticalviewpager.transforms;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-27-14:57
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ZoomOutTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.90f;

    @Override
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();
        float alpha = 0;
        if (0 <= position && position <= 1) {
            alpha = 1 - position;
        } else if (-1 < position && position < 0) {
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float verticalMargin = pageHeight * (1 - scaleFactor) / 2;
            float horizontalMargin = pageWidth * (1 - scaleFactor) / 2;
            if (position < 0) {
                view.setTranslationX(horizontalMargin - verticalMargin / 2);
            } else {
                view.setTranslationX(-horizontalMargin + verticalMargin / 2);
            }

            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

            alpha = position + 1;
        }

        view.setAlpha(alpha);
        view.setTranslationX(view.getWidth() * -position);
        float yPosition = position * view.getHeight();
        view.setTranslationY(yPosition);
    }

}