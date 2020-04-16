package com.didichuxing.doraemonkit.kit.webdoor;

import android.content.Context;
import android.content.Intent;

import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.constant.CachesKey;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.core.UniversalActivity;
import com.didichuxing.doraemonkit.util.CacheUtils;

import java.util.ArrayList;

/**
 * Created by wanglikun on 2018/10/10.
 */

public class WebDoorManager {
    private static final String TAG = "WebDoorManager";
    private WebDoorCallback mWebDoorCallback = new DefaultWebDoorCallback();
    private ArrayList<String> mHistory;

    public WebDoorCallback getWebDoorCallback() {
        return mWebDoorCallback;
    }

    public void setWebDoorCallback(WebDoorCallback webDoorCallback) {
        mWebDoorCallback = webDoorCallback;
    }

    public void removeWebDoorCallback() {
        mWebDoorCallback = null;
    }

    public void saveHistory(Context context, String text) {
        if (mHistory == null) {
            mHistory = (ArrayList<String>) CacheUtils.readObject(context, CachesKey.WEB_DOOR_HISTORY);
        }
        if (mHistory == null) {
            mHistory = new ArrayList<>();
        }
        if (mHistory.contains(text)) {
            return;
        }
        if (mHistory.size() == 5) {
            mHistory.remove(0);
        }
        mHistory.add(text);
        CacheUtils.saveObject(context, CachesKey.WEB_DOOR_HISTORY, mHistory);
    }

    public ArrayList<String> getHistory(Context context) {
        if (mHistory == null) {
            mHistory = (ArrayList<String>) CacheUtils.readObject(context, CachesKey.WEB_DOOR_HISTORY);
        }
        if (mHistory == null) {
            mHistory = new ArrayList<>();
        }
        return mHistory;
    }

    public void clearHistory(Context context) {
        mHistory.clear();
        CacheUtils.saveObject(context, CachesKey.WEB_DOOR_HISTORY, mHistory);
    }

    private static class Holder {
        private static WebDoorManager INSTANCE = new WebDoorManager();
    }

    public static WebDoorManager getInstance() {
        return Holder.INSTANCE;
    }

    public interface WebDoorCallback {
        void overrideUrlLoading(Context context, String url);
    }

    private class DefaultWebDoorCallback implements WebDoorCallback {

        @Override
        public void overrideUrlLoading(Context context, String url) {
            Intent intent = new Intent(context, UniversalActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(BundleKey.FRAGMENT_INDEX, FragmentIndex.FRAGMENT_WEB_DOOR_DEFAULT);
            intent.putExtra(BundleKey.KEY_URL, url);
            context.startActivity(intent);
        }
    }
}