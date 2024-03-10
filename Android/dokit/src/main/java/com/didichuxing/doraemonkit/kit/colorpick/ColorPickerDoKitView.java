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

import com.didichuxing.doraemonkit.DoKit;
import com.didichuxing.doraemonkit.util.ActivityUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.core.DoKitManager;
import com.didichuxing.doraemonkit.kit.core.AbsDoKitView;
import com.didichuxing.doraemonkit.kit.core.DoKitViewLayoutParams;
import com.didichuxing.doraemonkit.util.DoKitImageUtil;
import com.didichuxing.doraemonkit.util.UIUtils;

/**
 * @author jintai
 * @date 2019/09/26
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ColorPickerDoKitView extends AbsDoKitView {

    private ImageCapture mImageCapture;
    private ColorPickerView mPickerView;
    private ColorPickerInfoDoKitView mInfoDokitView;
    private int width;
    private int height;
    private int statusBarHeight;

    private Runnable mRunnable;

    @Override
    public void onCreate(Context context) {
        ColorPickManager.getInstance().setColorPickerDokitView(this);
        mInfoDokitView = DoKit.getDoKitView(ActivityUtils.getTopActivity(), ColorPickerInfoDoKitView.class);
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
    public void initDokitViewLayoutParams(DoKitViewLayoutParams params) {
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        params.height = DoKitViewLayoutParams.WRAP_CONTENT;
        params.width = DoKitViewLayoutParams.WRAP_CONTENT;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageCapture.destroy();
    }

    /**
     * 显示像素信息
     */
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
        int colorInt = DoKitImageUtil.getPixel(bitmap, xCenter, yCenter);
        mPickerView.setBitmap(bitmap, colorInt, startX, startY);
        mInfoDokitView.showInfo(colorInt, startX, startY);
    }

    /**
     * 捕捉截图信息
     */
    private void captureInfo(int delay) {

        mRunnable = new Runnable() {
            @Override
            public void run() {
                mImageCapture.capture();
                //截图完成以后恢复
                mPickerView.setVisibility(View.VISIBLE);
                showInfo();
            }
        };
        //先隐藏拾色器控件 否则会把拾色器也截图进去
        mPickerView.setVisibility(View.INVISIBLE);
        getDoKitView().postDelayed(mRunnable, delay);
    }


    @Override
    public void onDown(int x, int y) {
        super.onDown(x, y);
        captureInfo(100);
    }

    @Override
    public void onMove(int x, int y, int dx, int dy) {
        super.onMove(x, y, dx, dy);
        if (isNormalMode() && getNormalLayoutParams() != null) {
            checkBound(getNormalLayoutParams());
        } else {
            if (getSystemLayoutParams() != null) {
                checkBound(getSystemLayoutParams());
            }
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
        immInvalidate();
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
        if (DoKitManager.IS_NORMAL_FLOAT_MODE) {
            getDoKitView().removeCallbacks(mRunnable);
        }
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
