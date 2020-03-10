package com.didichuxing.doraemonkit.kit.uiperformance;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorTreeAdapter;

import com.didichuxing.doraemonkit.model.ViewInfo;
import com.didichuxing.doraemonkit.util.LogHelper;
import com.didichuxing.doraemonkit.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020-01-07-11:17
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class UIPerformanceUtil {
    private static final String TAG = "UIPerformanceUtil";

    public static List<ViewInfo> getViewInfos(Activity activity) {
        if (activity == null) {
            LogHelper.d(TAG, "resume activity is null");
            return new ArrayList<>();
        }
        if (activity.getWindow() == null) {
            LogHelper.d(TAG, "resume activity window is null");
            return new ArrayList<>();
        }

        return getViewInfos(UIUtils.getDokitAppContentView(activity));
    }

    private static List<ViewInfo> getViewInfos(View view) {
        List<ViewInfo> infos = new ArrayList<>();
        traverseViews(view, infos, 0);
        return infos;
    }

    private static void traverseViews(View view, List<ViewInfo> infos, int layerNum) {
        if (view == null) {
            return;
        }
        layerNum++;
        if (view instanceof ViewGroup) {
            int childCount = ((ViewGroup) view).getChildCount();
            if (childCount != 0) {
                for (int index = childCount - 1; index >= 0; index--) {
                    traverseViews(((ViewGroup) view).getChildAt(index), infos, layerNum);
                }
            }
        } else {
            long startTime = System.nanoTime();
            //view.draw(mPerformanceCanvas);
            long endTime = System.nanoTime();
            float time = (endTime - startTime) / 10_000 / 100f;
            //LogHelper.d(TAG, "drawTime: " + time + " ms");
            ViewInfo viewInfo = new ViewInfo(view);
            viewInfo.drawTime = time;
            viewInfo.layerNum = layerNum;
            infos.add(viewInfo);
        }
    }

}
