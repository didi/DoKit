package com.didichuxing.doraemonkit.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.annotation.AnyRes;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.ui.layoutborder.ViewBorderFrameLayout;

import java.lang.reflect.Method;
import java.security.PublicKey;

/**
 * Created by zhangweida on 2018/6/22.
 */

public class UIUtils {
    private static final String TAG = "UIUtils";

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static float px2dp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static float getDensity(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) {
            return 0;
        }
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics.density;
    }

    public static int getDensityDpi(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) {
            return 0;
        }
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics.densityDpi;
    }

    public static int getWidthPixels(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) {
            return 0;
        }
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public static int getHeightPixels(Context context) {
        return getRealHeightPixels(context) - getStatusBarHeight(context);
    }

    public static int getRealHeightPixels(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
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

    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
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
            rect.top -= UIUtils.getStatusBarHeight(view.getContext());
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
                }
            }
        }
        return out.toString();
    }


    /**
     * 获得app的contentView
     *
     * @param activity
     * @return
     */
    public static View getDokitAppContentView(Activity activity) {
        View mAppContentView = activity.findViewById(R.id.dokit_app_contentview_id);
        if (mAppContentView != null) {
            return mAppContentView;
        }
        FrameLayout decorView = (FrameLayout) activity.getWindow().getDecorView();

        for (int index = 0; index < decorView.getChildCount(); index++) {
            View child = decorView.getChildAt(index);
            //解决与布局边框工具冲突的问题
            if (child instanceof LinearLayout || child instanceof ViewBorderFrameLayout) {
                if (child instanceof ViewBorderFrameLayout) {
                    mAppContentView = ((ViewBorderFrameLayout) child).getChildAt(0);
                } else {
                    mAppContentView = child;
                }
                break;
            }
        }

        return mAppContentView;
    }

    private static boolean resourceHasPackage(@AnyRes int resid) {
        return (resid >>> 24) != 0;
    }
}
