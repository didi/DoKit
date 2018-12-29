package com.didichuxing.doraemondemo;

import android.app.Application;
import android.os.StrictMode;

/**
 * Created by zhangweida on 2018/6/22.
 */

public class App extends Application {
    private static final String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();
//        DoraemonKit.install(this);
//        DoraemonKit.setWebDoorCallback(new WebDoorManager.WebDoorCallback() {
//            @Override
//            public void overrideUrlLoading(String url) {
//                Intent intent = new Intent(App.this, WebViewActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra(WebViewActivity.KEY_URL, url);
//                startActivity(intent);
//            }
//        });
        StrictMode.enableDefaults();
    }
}