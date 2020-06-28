package com.didichuxing.doraemonkit.kit.colorpick;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.ActivityUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.core.AbsDokitView;
import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams;
import com.didichuxing.doraemonkit.kit.core.DokitViewManager;
import com.didichuxing.doraemonkit.util.ImageUtil;
import com.didichuxing.doraemonkit.util.UIUtils;

/**
 * @author jintai
 * @date 2019/09/26
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ColorPickerDokitView extends AbsDokitView {

    private ImageCapture mImageCapture;
    private ColorPickerView mPickerView;
    private ColorPickerInfoDokitView mInfoDokitView;
    private int width;
    private int height;
    private int statusBarHeight;

    @Override
    public void onCreate(Context context) {
        ColorPickManager.getInstance().setColorPickerDokitView(this);
        mInfoDokitView = (ColorPickerInfoDokitView) DokitViewManager.getInstance().getDokitView(ActivityUtils.getTopActivity(), ColorPickerInfoDokitView.class.getSimpleName());
        mImageCapture = new ImageCapture();
        try {
            mImageCapture.init(context, getBundle(), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 当服务准备好
     */
    void onScreenServiceReady() {
        mImageCapture.initImageRead(ColorPickManager.getInstance().getMediaProjection());
    }


    @Override
    public void onViewCreated(FrameLayout view) {
        initView();
    }

    private void initView() {
        mPickerView = findViewById(R.id.picker_view);
        ViewGroup.LayoutParams params = mPickerView.getLayoutParams();
        //大小必须是2的倍数
        params.width = ColorPickConstants.PICK_VIEW_SIZE;
        params.height = ColorPickConstants.PICK_VIEW_SIZE;
        mPickerView.setLayoutParams(params);

        width = UIUtils.getWidthPixels();
        height = UIUtils.getHeightPixels();
        statusBarHeight = UIUtils.getStatusBarHeight();
        captureInfo(500);
    }

    @Override
    public View onCreateView(Context context, FrameLayout view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_color_picker, null);
    }


    @Override
    public void initDokitViewLayoutParams(DokitViewLayoutParams params) {
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        params.height = DokitViewLayoutParams.WRAP_CONTENT;
        params.width = DokitViewLayoutParams.WRAP_CONTENT;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageCapture.destroy();
    }

    private void showInfo() {
        int x, y;
        if (isNormalMode()) {
            x = getNormalLayoutParams().leftMargin;
            y = getNormalLayoutParams().topMargin;
        } else {
            x = getSystemLayoutParams().x;
            y = getSystemLayoutParams().y;
        }

        int pickAreaSize = ColorPickConstants.PICK_AREA_SIZE;
        int startX = x + ColorPickConstants.PICK_VIEW_SIZE / 2 - pickAreaSize / 2;
        int startY = y + ColorPickConstants.PICK_VIEW_SIZE / 2 - pickAreaSize / 2 + UIUtils.getStatusBarHeight();
        Bitmap bitmap = mImageCapture.getPartBitmap(startX, startY, pickAreaSize, pickAreaSize);
        if (bitmap == null) {
            return;
        }
        int xCenter = bitmap.getWidth() / 2;
        int yCenter = bitmap.getHeight() / 2;
        int colorInt = ImageUtil.getPixel(bitmap, xCenter, yCenter);
        mPickerView.setBitmap(bitmap, colorInt, startX, startY);
        mInfoDokitView.showInfo(colorInt, startX, startY);
    }

    /**
     * 捕捉截图信息
     */
    private void captureInfo(int delay) {
        //先隐藏拾色器控件 否则会把拾色器也截图进去
        mPickerView.setVisibility(View.INVISIBLE);
        getRootView().postDelayed(new Runnable() {
            @Override
            public void run() {
                mImageCapture.capture();
                //截图完成以后恢复
                mPickerView.setVisibility(View.VISIBLE);
                showInfo();
            }
        }, delay);
    }


    @Override
    public void onDown(int x, int y) {
        super.onDown(x, y);
        captureInfo(100);
    }

    @Override
    public void onMove(int x, int y, int dx, int dy) {
        super.onMove(x, y, dx, dy);
        if (isNormalMode()) {
            checkBound(getNormalLayoutParams());
        } else {
            checkBound(getSystemLayoutParams());
        }
        showInfo();
    }

    private void checkBound(FrameLayout.LayoutParams layoutParams) {
        if (layoutParams.leftMargin < -mPickerView.getWidth() / 2) {
            layoutParams.leftMargin = -mPickerView.getWidth() / 2;
        }
        if (layoutParams.leftMargin > width - mPickerView.getWidth() / 2 - ColorPickConstants.PIX_INTERVAL) {
            layoutParams.leftMargin = width - mPickerView.getWidth() / 2 - ColorPickConstants.PIX_INTERVAL;
        }
        if (layoutParams.topMargin < -mPickerView.getHeight() / 2 - statusBarHeight) {
            layoutParams.topMargin = -mPickerView.getHeight() / 2 - statusBarHeight;
        }
        if (layoutParams.topMargin > height - mPickerView.getHeight() / 2 - ColorPickConstants.PIX_INTERVAL) {
            layoutParams.topMargin = height - mPickerView.getHeight() / 2 - ColorPickConstants.PIX_INTERVAL;
        }
        layoutParams.width = ColorPickConstants.PICK_VIEW_SIZE;
        layoutParams.height = ColorPickConstants.PICK_VIEW_SIZE;
        invalidate();
    }

    private void checkBound(WindowManager.LayoutParams layoutParams) {
        if (layoutParams.x < -mPickerView.getWidth() / 2) {
            layoutParams.x = -mPickerView.getWidth() / 2;
        }
        if (layoutParams.x > width - mPickerView.getWidth() / 2 - ColorPickConstants.PIX_INTERVAL) {
            layoutParams.x = width - mPickerView.getWidth() / 2 - ColorPickConstants.PIX_INTERVAL;
        }
        if (layoutParams.y < -mPickerView.getHeight() / 2 - statusBarHeight) {
            layoutParams.y = -mPickerView.getHeight() / 2 - statusBarHeight;
        }
        if (layoutParams.y > height - mPickerView.getHeight() / 2 - ColorPickConstants.PIX_INTERVAL) {
            layoutParams.y = height - mPickerView.getHeight() / 2 - ColorPickConstants.PIX_INTERVAL;
        }
    }

    @Override
    public void onEnterBackground() {
        //不需要调用父类方法 隐藏
    }

    @Override
    public void onEnterForeground() {
        //不需要调用父类方法 显示
    }


    @Override
    public boolean restrictBorderline() {
        return false;
    }


}