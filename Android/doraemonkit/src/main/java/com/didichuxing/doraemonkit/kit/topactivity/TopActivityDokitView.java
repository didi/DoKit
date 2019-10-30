package com.didichuxing.doraemonkit.kit.topactivity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.TopActivityConfig;
import com.didichuxing.doraemonkit.ui.base.AbsDokitView;
import com.didichuxing.doraemonkit.ui.base.DokitViewLayoutParams;
import com.didichuxing.doraemonkit.ui.base.DokitViewManager;

/**
 * 项目名:    Android
 * 包名       com.didichuxing.doraemonkit.kit.topactivity
 * 文件名:    TopActivityFloatPage
 * 创建时间:  2019-09-26 on 13:38
 * 描述:
 *
 * @author jintai
 */

public class TopActivityDokitView extends AbsDokitView implements Application.ActivityLifecycleCallbacks {

    private TextView className;
    private TextView pkgName;
    private TextView pathName;
    private Application app;

    @Override
    public void onCreate(Context context) {
        app = (Application) context.getApplicationContext();
        app.registerActivityLifecycleCallbacks(this);
    }

    @Override
    public View onCreateView(Context context, FrameLayout view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_view_top_activity, null);
    }

    @Override
    public void onViewCreated(FrameLayout view) {
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TopActivityConfig.setTopActivityOpen(getContext(), false);
                DokitViewManager.getInstance().detach(TopActivityDokitView.class);
            }
        });
        pkgName = findViewById(R.id.pkg_name);
        className = findViewById(R.id.class_name);
        pathName = findViewById(R.id.path_name);
    }



    @Override
    public void initDokitViewLayoutParams(DokitViewLayoutParams params) {
        params.flags = DokitViewLayoutParams.FLAG_NOT_FOCUSABLE;
        params.width = DokitViewLayoutParams.MATCH_PARENT;
        params.height = DokitViewLayoutParams.WRAP_CONTENT;
        params.x = 0;
        params.y = 0;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        pkgName.setText(activity.getPackageName());
        className.setText(activity.getClass().getSimpleName());
        pathName.setText(activity.getClass().getName());
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
    public void onDestroy() {
        super.onDestroy();
        app.unregisterActivityLifecycleCallbacks(this);
    }
}
