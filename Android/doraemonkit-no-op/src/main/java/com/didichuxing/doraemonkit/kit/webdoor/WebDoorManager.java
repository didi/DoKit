package com.didichuxing.doraemonkit.kit.webdoor;

import android.content.Context;

/**
 * Created by wanglikun on 2018/10/10.
 */

public class WebDoorManager {
    public interface WebDoorCallback {
        void overrideUrlLoading(Context context, String url);
    }
}