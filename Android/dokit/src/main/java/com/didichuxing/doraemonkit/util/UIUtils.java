package com.didichuxing.doraemonkit.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.AnyRes;

import com.didichuxing.doraemonkit.DoKitEnv;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.layoutborder.ViewBorderFrameLayout;

import java.lang.reflect.Method;
import java.math.BigDecimal;

/**
 * Created by zhangweida on 2018/6/22.
 */

public class UIUtils {
    private static final String TAG = "UIUtils";

    public static int dp2px(float dpValue) {
        return ConvertUtils.dp2px(dpValue);
    }

    public static int px2dp(int px) {
        return ConvertUtils.px2dp(px);
    }

    public static float getDensity() {
        return ScreenUtils.getScreenDensity();
    }

    public static int getDensityDpi() {
        return ScreenUtils.getScreenDensityDpi();
    }

    public static int getWidthPixels() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) DoKitEnv.requireApp().getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) {
            return 0;
        }
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public static int getHeightPixels() {
        return getRealHeightPixels() - getStatusBarHeight();
    }

    public static int getRealHeightPixels() {
        WindowManager windowManager = (WindowManager) DoKitEnv.requireApp().getSystemService(Context.WINDOW_SERVICE);
        int height = 0;
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        Class c;
        try {
            c = Class.forName("android.view.Display");
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            height = dm.heightPixels;
        } catch (Exception e) {
            LogHelper.d(TAG, e.toString());
        }
        return height;
    }

    public static int getStatusBarHeight() {
        Resources resources = DoKitEnv.requireApp().getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    public static Rect getViewRect(View view) {
        Rect rect = new Rect();
        int[] locations = new int[2];
        view.getLocationOnScreen(locations);
        rect.left = locations[0];
        rect.top = locations[1];
        if (!checkStatusBarVisible(view.getContext())) {
            rect.top -= UIUtils.getStatusBarHeight();
        }
        rect.right = rect.left + view.getWidth();
        rect.bottom = rect.top + view.getHeight();
        return rect;
    }

    public static boolean checkStatusBarVisible(Context context) {
        return checkFullScreenByTheme(context) || checkFullScreenByCode(context) || checkFullScreenByCode2(context);
    }

    public static boolean checkFullScreenByTheme(Context context) {
        Resources.Theme theme = context.getTheme();
        if (theme != null) {
            TypedValue typedValue = new TypedValue();
            boolean result = theme.resolveAttribute(android.R.attr.windowFullscreen, typedValue, false);
            if (result) {
                typedValue.coerceToString();
                if (typedValue.type == TypedValue.TYPE_INT_BOOLEAN) {
                    return typedValue.data != 0;
                }
            }
        }
        return false;
    }

    public static boolean checkFullScreenByCode(Context context) {
        if (context instanceof Activity) {
            Window window = ((Activity) context).getWindow();
            if (window != null) {
                View decorView = window.getDecorView();
                if (decorView != null) {
                    return (decorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_FULLSCREEN) == View.SYSTEM_UI_FLAG_FULLSCREEN;
                }
            }
        }
        return false;
    }

    public static boolean checkFullScreenByCode2(Context context) {
        if (context instanceof Activity) {
            return (((Activity) context).getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN;
        }
        return false;
    }

    /**
     * 要特别注意 返回的字段包含空格  做判断时一定要trim()
     *
     * @param view
     * @return
     */
    public static String getIdText(View view) {
        final int id = view.getId();
        StringBuilder out = new StringBuilder();
        if (id != View.NO_ID) {
            final Resources r = view.getResources();
            if (id > 0 && resourceHasPackage(id) && r != null) {
                try {
                    String pkgname;
                    switch (id & 0xff000000) {
                        case 0x7f000000:
                            pkgname = "app";
                            break;
                        case 0x01000000:
                            pkgname = "android";
                            break;
                        default:
                            pkgname = r.getResourcePackageName(id);
                            break;
                    }
                    String typename = r.getResourceTypeName(id);
                    String entryname = r.getResourceEntryName(id);
                    out.append(" ");
                    out.append(pkgname);
                    out.append(":");
                    out.append(typename);
                    out.append("/");
                    out.append(entryname);
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return TextUtils.isEmpty(out.toString()) ? "" : out.toString();
    }

    /**
     * 去除前缀
     *
     * @param view
     * @return
     */
    public static String getRealIdText(View view) {
        String id = getIdText(view);
        if (id.isEmpty()) {
            return "-1";
        } else {
            return id.split("/")[1];
        }
    }

    /**
     * ViewBorderFrameLayout 的str id
     */
    private final static String STR_VIEW_BORDER_Id = "app:id/dokit_view_border_id";

    /**
     * 获得app的contentView
     *
     * @param activity
     * @return
     */
    public static View getDokitAppContentView(Activity activity) {
        FrameLayout decorView = (FrameLayout) activity.getWindow().getDecorView();
        View mAppContentView = (View) decorView.getTag(R.id.dokit_app_contentview_id);
        if (mAppContentView != null) {
            return mAppContentView;
        }
        for (int index = 0; index < decorView.getChildCount(); index++) {
            View child = decorView.getChildAt(index);
            //LogHelper.i(TAG, "childId=====>" + getIdText(child));
            //解决与布局边框工具冲突的问题
            if ((child instanceof LinearLayout && TextUtils.isEmpty(getIdText(child).trim())) || child instanceof FrameLayout) {
                //如果是DokitBorderView 则返回他下面的第一个子child
                if (getIdText(child).trim().equals(STR_VIEW_BORDER_Id)) {
                    mAppContentView = ((ViewBorderFrameLayout) child).getChildAt(0);
                } else {
                    mAppContentView = child;
                }
                mAppContentView.setTag(R.id.dokit_app_contentview_id);
                break;
            }
        }

        return mAppContentView;
    }

    private static boolean resourceHasPackage(@AnyRes int resid) {
        return (resid >>> 24) != 0;
    }


    /**
     * 获取屏幕尺寸
     *
     * @param context
     * @return
     */
    public static double getScreenInch(Activity context) {
        double inch = 0;

        try {
            int realWidth = 0, realHeight = 0;
            Display display = context.getWindowManager().getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            if (android.os.Build.VERSION.SDK_INT >= 17) {
                Point size = new Point();
                display.getRealSize(size);
                realWidth = size.x;
                realHeight = size.y;
            } else if (android.os.Build.VERSION.SDK_INT < 17
                    && android.os.Build.VERSION.SDK_INT >= 14) {
                Method mGetRawH = Display.class.getMethod("getRawHeight");
                Method mGetRawW = Display.class.getMethod("getRawWidth");
                realWidth = (Integer) mGetRawW.invoke(display);
                realHeight = (Integer) mGetRawH.invoke(display);
            } else {
                realWidth = metrics.widthPixels;
                realHeight = metrics.heightPixels;
            }

            inch = formatDouble(Math.sqrt((realWidth / metrics.xdpi) * (realWidth / metrics.xdpi) + (realHeight / metrics.ydpi) * (realHeight / metrics.ydpi)), 1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return inch;
    }

    /**
     * Double类型保留指定位数的小数，返回double类型（四舍五入）
     * newScale 为指定的位数
     */
    private static double formatDouble(double d, int newScale) {
        BigDecimal bd = new BigDecimal(d);
        return bd.setScale(newScale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}
