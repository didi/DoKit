package com.didichuxing.doraemonkit.widget.easyrefresh.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.widget.easyrefresh.ILoadMoreView;
import com.github.ybq.android.spinkit.SpinKitView;



/**
 * Created by guanaj on 16/9/22.
 */

public class SimpleLoadMoreView extends FrameLayout implements ILoadMoreView {

    private TextView tvHitText;
    private SpinKitView spinKitView;
    private View view;

    public SimpleLoadMoreView(Context context) {
        this(context, null);
    }

    public SimpleLoadMoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        view = inflate(context, R.layout.dk_refresh_default_load_more, this);
        tvHitText = (TextView) view.findViewById(R.id.tv_hit_content);
        spinKitView = (SpinKitView) view.findViewById(R.id.spin_kit);

    }


    @Override
    public void reset() {
        spinKitView.setVisibility(INVISIBLE);
        tvHitText.setVisibility(INVISIBLE);
        tvHitText.setText("正在加载...");
    }

    @Override
    public void loading() {
        spinKitView.setVisibility(VISIBLE);
        tvHitText.setVisibility(VISIBLE);
        tvHitText.setText("正在加载...");
    }

    @Override
    public void loadComplete() {
        spinKitView.setVisibility(INVISIBLE);
        tvHitText.setVisibility(VISIBLE);
        tvHitText.setText("加载完成");

    }

    @Override
    public void loadFail() {
        spinKitView.setVisibility(INVISIBLE);
        tvHitText.setVisibility(VISIBLE);
        tvHitText.setText("加载失败,点击重新加载");

    }

    @Override
    public void loadNothing() {
        spinKitView.setVisibility(INVISIBLE);
        tvHitText.setVisibility(VISIBLE);
        tvHitText.setText("没有更多可以加载");
    }

    @Override
    public View getCanClickFailView() {
        return view;
    }


}
