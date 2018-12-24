package com.didichuxing.doraemonkit.kit.colorpick;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.PageTag;
import com.didichuxing.doraemonkit.ui.base.BaseFloatPage;
import com.didichuxing.doraemonkit.ui.base.FloatPageManager;
import com.didichuxing.doraemonkit.ui.colorpicker.ColorPickerView;
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

    private float sdX, sdY;
    private float ldX, ldY;

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
        return LayoutInflater.from(context).inflate(R.layout.float_color_picker, null);
    }

    @Override
    protected void onLayoutParamsCreated(WindowManager.LayoutParams params) {
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    @Override
    protected void onCreate(Context context) {
        mInfoFloatPage = (ColorPickerInfoFloatPage) FloatPageManager.getInstance().getFloatPage(PageTag.PAGE_COLOR_PICKER_INFO);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mImageCapture = new ImageCapture();
        mImageCapture.init(context, getBundle());
    }

    private void checkLayoutParams() {
        if (getLayoutParams().x < 0) {
            getLayoutParams().x = 0;
        } else if (getLayoutParams().x > UIUtils.getWidthPixels(getContext())) {
            getLayoutParams().x = UIUtils.getWidthPixels(getContext());
        }
        if (getLayoutParams().y < 0) {
            getLayoutParams().y = 0;
        } else if (getLayoutParams().y > UIUtils.getRealHeightPixels(getContext())) {
            getLayoutParams().y = UIUtils.getRealHeightPixels(getContext());
        }
    }

    @Override
    protected void onDestroy() {
        mImageCapture.destroy();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                captureInfo();
                showInfo(getLayoutParams().x + getRootView().getWidth() / 2, getLayoutParams().y + getRootView().getHeight() / 2);
                sdX = ldX = x;
                sdY = ldY = y;
                return false;
            case MotionEvent.ACTION_MOVE:
                getLayoutParams().x += (x - ldX + 0.5f);
                getLayoutParams().y += (y - ldY + 0.5f);
                ldX = x;
                ldY = y;
                checkLayoutParams();
                showInfo(x, y);
                mWindowManager.updateViewLayout(getRootView(), getLayoutParams());
                return false;
            case MotionEvent.ACTION_UP:
                int mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                if (Math.abs(x - sdX) <= mTouchSlop && Math.abs(y - sdY) <= mTouchSlop) {
                    return false;
                }
                return true;
            default:
                break;
        }
        return false;
    }

    private void showInfo(float x, float y) {
        if (mImageCapture.getBitmap() == null) {
            return;
        }
        float posX = x - getResources().getDimensionPixelSize(R.dimen.dk_dp_6);
        float posY = y - getResources().getDimensionPixelSize(R.dimen.dk_dp_6);
        int width = UIUtils.dp2px(getContext(), getResources().getDimensionPixelSize(R.dimen.dk_dp_6));
        int height = UIUtils.dp2px(getContext(), getResources().getDimensionPixelSize(R.dimen.dk_dp_6));
        if (width + posX > mImageCapture.getBitmap().getWidth() || height + posY > mImageCapture.getBitmap().getHeight() || posX < 0 || posY < 0) {
            return;
        }
        Bitmap bitmap = Bitmap.createBitmap(mImageCapture.getBitmap(), (int) posX, (int) posY, width, height);
        mPickerView.setBitmap(bitmap);
        int xCenter = bitmap.getWidth() / 2;
        int yCenter = bitmap.getHeight() / 2;
        int colorInt = bitmap.getPixel(xCenter, yCenter);
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
}