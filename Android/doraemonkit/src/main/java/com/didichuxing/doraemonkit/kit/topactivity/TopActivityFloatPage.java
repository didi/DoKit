package com.didichuxing.doraemonkit.kit.topactivity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.TopActivityConfig;
import com.didichuxing.doraemonkit.ui.base.BaseFloatPage;
import com.didichuxing.doraemonkit.ui.base.FloatPageManager;
import com.didichuxing.doraemonkit.ui.base.TouchProxy;

/**
 * 项目名:    Android
 * 包名       com.didichuxing.doraemonkit.kit.topactivity
 * 文件名:    TopActivityFloatPage
 * 创建时间:  2019-04-29 on 13:38
 * 描述:
 *
 * @author 阿钟
 */

public class TopActivityFloatPage extends BaseFloatPage implements TouchProxy.OnTouchEventListener,
        Application.ActivityLifecycleCallbacks {

    private TouchProxy touchProxy = new TouchProxy(this);
    private WindowManager windowManager;
    private TextView className;
    private TextView pkgName;
    private TextView pathName;
    private Application app;

    @Override
    protected void onCreate(Context context) {
        super.onCreate(context);
        app = (Application) context.getApplicationContext();
        app.registerActivityLifecycleCallbacks(this);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    protected View onCreateView(Context context, ViewGroup view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_view_top_activity, null);
    }

    @Override
    protected void onViewCreated(View view) {
        super.onViewCreated(view);
        getRootView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return touchProxy.onTouchEvent(v, event);
            }
        });
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TopActivityConfig.setTopActivityOpen(getContext(), false);
                FloatPageManager.getInstance().removeAll(TopActivityFloatPage.class);
            }
        });
        pkgName = findViewById(R.id.pkg_name);
        className = findViewById(R.id.class_name);
        pathName = findViewById(R.id.path_name);
    }

    @Override
    protected void onLayoutParamsCreated(WindowManager.LayoutParams params) {
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.x = 0;
        params.y = 0;
    }

    @Override
    public void onEnterForeground() {
        super.onEnterForeground();
        getRootView().setVisibility(View.VISIBLE);
    }

    @Override
    public void onEnterBackground() {
        super.onEnterBackground();
        getRootView().setVisibility(View.GONE);
    }

    @Override
    public void onMove(int x, int y, int dx, int dy) {
        getLayoutParams().x += dx;
        getLayoutParams().y += dy;
        windowManager.updateViewLayout(getRootView(), getLayoutParams());
    }


    @Override
    public void onActivityResumed(Activity activity) {
        pkgName.setText(activity.getPackageName());
        className.setText(activity.getClass().getSimpleName());
        pathName.setText(activity.getClass().getName());
    }

    @Override
    public void onUp(int x, int y) {

    }

    @Override
    public void onDown(int x, int y) {

    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        app.unregisterActivityLifecycleCallbacks(this);
    }
}
