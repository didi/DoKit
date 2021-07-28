package com.didichuxing.doraemonkit.kit.webdoor;

import android.content.Context;

import com.didichuxing.doraemonkit.DoKit;
import com.didichuxing.doraemonkit.constant.CachesKey;
import com.didichuxing.doraemonkit.kit.webview.WebViewManager;
import com.didichuxing.doraemonkit.util.DoKitCacheUtils;

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

    public void saveHistory(String text) {
        if (mHistory == null) {
            mHistory = (ArrayList<String>) DoKitCacheUtils.readObject(CachesKey.WEB_DOOR_HISTORY);
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
        DoKitCacheUtils.saveObject(CachesKey.WEB_DOOR_HISTORY, mHistory);
    }

    public ArrayList<String> getHistory() {
        if (mHistory == null) {
            mHistory = (ArrayList<String>) DoKitCacheUtils.readObject(CachesKey.WEB_DOOR_HISTORY);
        }
        if (mHistory == null) {
            mHistory = new ArrayList<>();
        }
        return mHistory;
    }

    public void clearHistory() {
        mHistory.clear();
        DoKitCacheUtils.saveObject(CachesKey.WEB_DOOR_HISTORY, mHistory);
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
            WebViewManager.INSTANCE.setUrl(url);
            DoKit.launchFullScreen(WebDoorDefaultFragment.class, context);

        }
    }
}