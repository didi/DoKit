package com.didichuxing.doraemonkit.ui.base;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.WindowManager;

import com.didichuxing.doraemonkit.util.LogHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wanglikun on 2018/10/23.
 */

public class FloatPageManager {
    private static final String TAG = "FloatPageManager";

    private WindowManager mWindowManager;
    private Context mContext;
    private List<BaseFloatPage> mPages = new ArrayList<>();

    public void notifyBackground() {
        for (BaseFloatPage page : mPages) {
            page.onEnterBackground();
        }
    }

    public void notifyForeground() {
        for (BaseFloatPage page : mPages) {
            page.onEnterForeground();
        }
    }

    private static class Holder {
        private static FloatPageManager INSTANCE = new FloatPageManager();
    }

    public static FloatPageManager getInstance() {
        return Holder.INSTANCE;
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public void add(PageIntent pageIntent) {
        try {
            if (pageIntent.targetClass == null) {
                return;
            }
            if (pageIntent.mode == PageIntent.MODE_SINGLE_INSTANCE) {
                for (BaseFloatPage page : mPages) {
                    if (pageIntent.targetClass.isInstance(page)) {
                        return;
                    }
                }
            }
            BaseFloatPage page = pageIntent.targetClass.newInstance();
            page.setBundle(pageIntent.bundle);
            page.setTag(pageIntent.tag);
            mPages.add(page);
            page.performCreate(mContext);
            mWindowManager.addView(page.getRootView(),
                    page.getLayoutParams());
        } catch (InstantiationException e) {
            LogHelper.e(TAG, e.toString());
        } catch (IllegalAccessException e) {
            LogHelper.e(TAG, e.toString());
        }
    }

    public void remove(String tag) {
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        for (BaseFloatPage page : mPages) {
            if (tag.equals(page.getTag())) {
                mWindowManager.removeView(page.getRootView());
                page.performDestroy();
                mPages.remove(page);
                return;
            }
        }
    }

    public void remove(BaseFloatPage page) {
        mWindowManager.removeView(page.getRootView());
        page.performDestroy();
        mPages.remove(page);
    }

    public void removeAll(Class<? extends BaseFloatPage> pageClass) {
        for (Iterator<BaseFloatPage> it = mPages.iterator(); it.hasNext(); ) {
            BaseFloatPage page = it.next();
            if (pageClass.isInstance(page)) {
                mWindowManager.removeView(page.getRootView());
                page.performDestroy();
                it.remove();
            }
        }
    }

    public void removeAll() {
        for (Iterator<BaseFloatPage> it = mPages.iterator(); it.hasNext(); ) {
            BaseFloatPage page = it.next();
            mWindowManager.removeView(page.getRootView());
            page.performDestroy();
            it.remove();
        }
    }

    public BaseFloatPage getFloatPage(String tag) {
        if (TextUtils.isEmpty(tag)) {
            return null;
        }
        for (BaseFloatPage page : mPages) {
            if (tag.equals(page.getTag())) {
                return page;
            }
        }
        return null;
    }
}
