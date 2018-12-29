package com.didichuxing.doraemonkit.kit.colorpick;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.PageTag;
import com.didichuxing.doraemonkit.ui.base.BaseFloatPage;
import com.didichuxing.doraemonkit.ui.base.FloatPageManager;
import com.didichuxing.doraemonkit.ui.base.TouchProxy;
import com.didichuxing.doraemonkit.ui.colorpicker.ColorPickerView;
import com.didichuxing.doraemonkit.util.ImageUtil;
import com.didichuxing.doraemonkit.util.UIUtils;

/**
 * Created by wanglikun on 2018/9/13.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ColorPickerFloatPage extends BaseFloatPage implements View.OnTouchListener {
    private static final String TAG = "ColorPickerFloatPage";

    private WindowManager mWindowManager;
    private ImageCapture mImageCapture;
    private ColorPickerView mPickerView;
    private ColorPickerInfoFloatPage mInfoFloatPage;
    private TouchProxy mTouchProxy;

    @Override
    protected void onViewCreated(View view) {
        initView();
    }

    private void initView() {
        mPickerView = findViewById(R.id.picker_view);
        getRootView().setOnTouchListener(this);
    }

    @Override
    protected View onCreateView(Context context, ViewGroup view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_color_picker, null);
    }

    @Override
    protected void onLayoutParamsCreated(WindowManager.LayoutParams params) {
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    @Override
    protected void onCreate(Context context) {
        mTouchProxy = new TouchProxy(new TouchProxy.OnTouchEventListener() {
            @Override
            public void onMove(int x, int y, int dx, int dy) {
                getLayoutParams().x += dx;
                getLayoutParams().y += dy;
                mWindowManager.updateViewLayout(getRootView(), getLayoutParams());
                showInfo();
            }

            @Override
            public void onUp(int x, int y) {

            }

            @Override
            public void onDown(int x, int y) {
                captureInfo();
            }
        });
        mInfoFloatPage = (ColorPickerInfoFloatPage) FloatPageManager.getInstance().getFloatPage(PageTag.PAGE_COLOR_PICKER_INFO);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mImageCapture = new ImageCapture();
        mImageCapture.init(context, getBundle());
    }

    @Override
    protected void onDestroy() {
        mImageCapture.destroy();
    }

    private void showInfo() {
        int x = getLayoutParams().x;
        int y = getLayoutParams().y;
        int pickAreaSize = getResources().getDimensionPixelSize(R.dimen.dk_dp_15);
        int startX = x + getRootView().getWidth() / 2 - pickAreaSize / 2;
        int startY = y + getRootView().getHeight() / 2 - pickAreaSize / 2 + UIUtils.getStatusBarHeight(getContext());
        Bitmap bitmap = mImageCapture.getPartBitmap(startX, startY, pickAreaSize, pickAreaSize);
        if (bitmap == null) {
            return;
        }
        int xCenter = bitmap.getWidth() / 2;
        int yCenter = bitmap.getHeight() / 2;
        int colorInt = ImageUtil.getPixel(bitmap, xCenter, yCenter);
        mPickerView.setBitmap(bitmap);
        mInfoFloatPage.showInfo(colorInt);
    }

    private void captureInfo() {
        getRootView().setVisibility(View.GONE);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mImageCapture.capture();
                getRootView().setVisibility(View.VISIBLE);
            }
        }, 100);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mTouchProxy.onTouchEvent(v, event);
    }
}