package com.didichuxing.doraemonkit.kit.performance.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.performance.datasource.IDataSource;

/**
 * @desc: 实时折线图
 */
public class LineChart extends FrameLayout {
    private TextView mTitle;
    private int performanceType;
    private CardiogramView mLine;

    public int getPerformanceType() {
        return performanceType;
    }

    public void setPerformanceType(int performanceType) {
        this.performanceType = performanceType;
    }

    public LineChart(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public LineChart(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.dk_view_line_chart, this);
        mTitle = findViewById(R.id.tv_title);
        mLine = findViewById(R.id.line_chart_view);
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void startMove() {
        mLine.startMove();
    }

    public void stopMove() {
        mLine.stopMove();
    }

    public void setInterval(int interval) {
        mLine.setInterval(interval);
    }

    public void setDataSource(@NonNull IDataSource dataSource) {
        mLine.setDataSource(dataSource);
    }

}
