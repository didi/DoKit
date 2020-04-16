package com.didichuxing.doraemonkit.widget.easyrefresh.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.widget.easyrefresh.IRefreshHeader;
import com.didichuxing.doraemonkit.widget.easyrefresh.State;



public class SimpleRefreshHeaderView extends FrameLayout implements IRefreshHeader {


    private Animation rotate_up;
    private Animation rotate_down;
    private Animation rotate_infinite;
    private TextView textView;
    private View arrowIcon;
    private View successIcon;
    private View loadingIcon;

    public SimpleRefreshHeaderView(Context context) {
        this(context, null);
    }

    public SimpleRefreshHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 初始化动画
        rotate_up = AnimationUtils.loadAnimation(context, R.anim.dk_easy_refresh_rotate_up);
        rotate_down = AnimationUtils.loadAnimation(context, R.anim.dk_easy_refresh_rotate_down);
        rotate_infinite = AnimationUtils.loadAnimation(context, R.anim.dk_easy_refresh_rotate_infinite);

        inflate(context, R.layout.dk_refresh_default_refresh_header, this);

        textView = (TextView) findViewById(R.id.text);
        arrowIcon = findViewById(R.id.arrowIcon);
        successIcon = findViewById(R.id.successIcon);
        loadingIcon = findViewById(R.id.loadingIcon);
    }

    @Override
    public void reset() {
        textView.setText(getResources().getText(R.string.dk_header_reset));
        successIcon.setVisibility(INVISIBLE);
        arrowIcon.setVisibility(VISIBLE);
        arrowIcon.clearAnimation();
        loadingIcon.setVisibility(INVISIBLE);
        loadingIcon.clearAnimation();
    }

    @Override
    public void pull() {

    }

    @Override
    public void refreshing() {
        arrowIcon.setVisibility(INVISIBLE);
        loadingIcon.setVisibility(VISIBLE);
        textView.setText(getResources().getText(R.string.dk_header_refreshing));
        arrowIcon.clearAnimation();
        loadingIcon.startAnimation(rotate_infinite);
    }

    @Override
    public void onPositionChange(float currentPos, float lastPos, float refreshPos, boolean isTouch, State state) {
        // 往上拉
        if (currentPos < refreshPos && lastPos >= refreshPos) {
            Log.i("", ">>>>up");
            if (isTouch && state == State.PULL) {
                textView.setText(getResources().getText(R.string.dk_header_pull));
                arrowIcon.clearAnimation();
                arrowIcon.startAnimation(rotate_down);
            }
            // 往下拉
        } else if (currentPos > refreshPos && lastPos <= refreshPos) {
            Log.i("", ">>>>down");
            if (isTouch && state == State.PULL) {
                textView.setText(getResources().getText(R.string.dk_header_pull_over));
                arrowIcon.clearAnimation();
                arrowIcon.startAnimation(rotate_up);
            }
        }
    }

    @Override
    public void complete() {
        loadingIcon.setVisibility(INVISIBLE);
        loadingIcon.clearAnimation();
        successIcon.setVisibility(VISIBLE);
        textView.setText(getResources().getText(R.string.dk_header_completed));
    }
}
