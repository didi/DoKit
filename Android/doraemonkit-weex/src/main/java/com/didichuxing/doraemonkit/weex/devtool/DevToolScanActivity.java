package com.didichuxing.doraemonkit.weex.devtool;

import androidx.annotation.IdRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar;
import com.didichuxing.doraemonkit.weex.R;
import com.didichuxing.doraemonkit.zxing.activity.CaptureActivity;

/**
 * @author haojianglong
 * @date 2019-06-18
 */
public class DevToolScanActivity extends CaptureActivity {

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initTitleBar();
    }

    @Override
    public void setContentView(@IdRes int layoutResID) {
        super.setContentView(layoutResID);
        initTitleBar();
    }

    private void initTitleBar() {
        HomeTitleBar homeTitleBar = new HomeTitleBar(this);
        homeTitleBar.setBackgroundColor(getResources().getColor(R.color.foreground_wtf));
        homeTitleBar.setTitle(getResources().getString(R.string.dk_dev_tool_title));
        homeTitleBar.setIcon(R.mipmap.dk_close_icon);
        homeTitleBar.setListener(new HomeTitleBar.OnTitleBarClickListener() {
            @Override
            public void onRightClick() {
                finish();
            }
        });
        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        (int) getResources().getDimension(R.dimen.dk_home_title_height));
        ((FrameLayout) findViewById(android.R.id.content)).addView(homeTitleBar, params);
    }

}
