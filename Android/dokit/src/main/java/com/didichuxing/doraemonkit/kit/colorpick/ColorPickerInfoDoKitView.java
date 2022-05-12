package com.didichuxing.doraemonkit.kit.colorpick;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.ColorInt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.didichuxing.doraemonkit.DoKit;
import com.didichuxing.doraemonkit.util.ActivityUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.ColorPickConfig;
import com.didichuxing.doraemonkit.kit.core.TranslucentActivity;
import com.didichuxing.doraemonkit.kit.core.AbsDoKitView;
import com.didichuxing.doraemonkit.kit.core.DoKitViewLayoutParams;
import com.didichuxing.doraemonkit.util.ColorUtil;
import com.didichuxing.doraemonkit.util.UIUtils;

/**
 * Created by jintai on 2019/09/26.
 */
public class ColorPickerInfoDoKitView extends AbsDoKitView {
    private ImageView mColor;
    private TextView mColorHex;
    private ImageView mClose;

    @Override
    public void onCreate(Context context) {
    }

    @Override
    public View onCreateView(Context context, FrameLayout view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_color_picker_info, null);
    }


    @Override
    public void initDokitViewLayoutParams(DoKitViewLayoutParams params) {
        params.width = getScreenShortSideLength();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.x = 0;
        params.y = UIUtils.getHeightPixels() - UIUtils.dp2px(95);
    }

    @Override
    public void onViewCreated(FrameLayout view) {
        initView();
    }

    private void initView() {
        mColor = findViewById(R.id.color);
        mColorHex = findViewById(R.id.color_hex);
        mClose = findViewById(R.id.close);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickManager.getInstance().setColorPickerDokitView(null);
                ColorPickConfig.setColorPickOpen(false);
                DoKit.removeFloating(ColorPickerDoKitView.class);
                DoKit.removeFloating(ColorPickerInfoDoKitView.class);
                //取色器kit是依赖在当前透明的Activity上的 所以关闭控件时需要finish
                if (ActivityUtils.getTopActivity() != null && ActivityUtils.getTopActivity() instanceof TranslucentActivity) {
                    ActivityUtils.getTopActivity().finish();
                }

            }
        });
    }

    public void showInfo(@ColorInt int colorInt, int x, int y) {
        mColor.setImageDrawable(new ColorDrawable(colorInt));
        mColorHex.setText(String.format(ColorPickConstants.TEXT_FOCUS_INFO, ColorUtil.parseColorInt(colorInt), x + ColorPickConstants.PIX_INTERVAL, y + ColorPickConstants.PIX_INTERVAL));
    }

    @Override
    public void onEnterBackground() {
        //不需要调用父类方法 隐藏
    }

    @Override
    public void onEnterForeground() {
        //不需要调用父类方法 显示
    }
}
