package com.didichuxing.doraemonkit.kit.uiperformance;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.didichuxing.doraemonkit.util.ActivityUtils;
import com.didichuxing.doraemonkit.model.ViewInfo;
import com.didichuxing.doraemonkit.util.LifecycleListenerUtil;
import com.didichuxing.doraemonkit.util.LogHelper;
import com.didichuxing.doraemonkit.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanglikun on 2019-06-27
 */
public class UIPerformanceManager implements LifecycleListenerUtil.LifecycleListener {
    private static final String TAG = "UIPerformanceManager";
    private Canvas mPerformanceCanvas;
    private List<PerformanceDataListener> mListeners = new ArrayList<>();

    private static class Holder {
        private static UIPerformanceManager INSTANCE = new UIPerformanceManager();
    }

    public static UIPerformanceManager getInstance() {
        return Holder.INSTANCE;
    }

    private UIPerformanceManager() {

    }

    public void start(Context context) {
        Bitmap canvasBitmap = Bitmap.createBitmap(UIUtils.getWidthPixels(), UIUtils.getHeightPixels(), Bitmap.Config.ARGB_8888);
        mPerformanceCanvas = new Canvas(canvasBitmap);
        LifecycleListenerUtil.registerListener(this);
    }

    public void stop() {
        mListeners.clear();
        mPerformanceCanvas = null;
        LifecycleListenerUtil.unRegisterListener(this);
    }

    public List<ViewInfo> getViewInfos(Activity activity) {
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

    private List<ViewInfo> getViewInfos(View view) {
        List<ViewInfo> infos = new ArrayList<>();
        traverseViews(view, infos, 0);
        return infos;
    }

    private void traverseViews(View view, List<ViewInfo> infos, int layerNum) {
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
            ViewInfo viewInfo = new ViewInfo(view);
            try {
                //页面不可见不进行渲染时间的统计,统计时候为0
                if (view.getVisibility() == View.VISIBLE) {
                    long startTime = System.nanoTime();
                    if (mPerformanceCanvas != null) {
                        view.draw(mPerformanceCanvas);
                    }
                    long endTime = System.nanoTime();
                    float time = (endTime - startTime) / 10_000 / 100f;
                    //LogHelper.d(TAG, "drawTime: " + time + " ms");
                    viewInfo.drawTime = time;
                    viewInfo.layerNum = layerNum;
                }
            } catch (Exception e) {
                //自定义View某些变量尚未初始化可能会崩溃
                //方便区分方法走到了异常,可以判断没有成功渲染设置值为-1
                viewInfo.drawTime = -1;
                viewInfo.layerNum = -1;
            }
            infos.add(viewInfo);
        }
    }

    public void addListener(PerformanceDataListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(PerformanceDataListener listener) {
        mListeners.remove(listener);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        for (PerformanceDataListener listener : mListeners) {
            listener.onRefresh(getViewInfos(activity));
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onFragmentAttached(Fragment f) {
        for (PerformanceDataListener listener : mListeners) {
            listener.onRefresh(getViewInfos(f.getActivity()));
        }
    }

    @Override
    public void onFragmentDetached(Fragment f) {
        for (PerformanceDataListener listener : mListeners) {
            listener.onRefresh(getViewInfos(f.getActivity()));
        }
    }

    public interface PerformanceDataListener {
        void onRefresh(List<ViewInfo> viewInfos);
    }

    /**
     * 初始化时直接显示显示层级
     */
    public void initRefresh() {
        for (PerformanceDataListener listener : mListeners) {
            listener.onRefresh(getViewInfos(ActivityUtils.getTopActivity()));
        }
    }

}