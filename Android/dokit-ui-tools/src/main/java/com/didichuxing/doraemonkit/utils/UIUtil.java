package com.didichuxing.doraemonkit.utils;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.didichuxing.doraemonkit.kit.layoutborder.ViewBorderFrameLayout;
import com.didichuxing.doraemonkit.uitools.R;
import com.didichuxing.doraemonkit.util.UIUtils;

/**
 * didi Create on 2022/8/12 .
 * <p>
 * Copyright (c) 2022/8/12 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/8/12 4:44 下午
 * @Description 用一句话说明文件功能
 */

public class UIUtil {

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
            if ((child instanceof LinearLayout && TextUtils.isEmpty(UIUtils.getIdText(child).trim())) || child instanceof FrameLayout) {
                //如果是DokitBorderView 则返回他下面的第一个子child
                if (UIUtils.getIdText(child).trim().equals(STR_VIEW_BORDER_Id)) {
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
}
