package com.didichuxing.doraemonkit.widget.chart;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.didichuxing.doraemonkit.R;

/**
 * @desc: 条形图
 */
public class BarChart extends LinearLayout {

    private TextView markFirst;
    private TextView markSecond;
    private TextView markThird;
    private View getValue;
    private View postValue;
    private View lineEnd;

    public BarChart(Context context) {
        super(context, null);
    }


    public BarChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public BarChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.dk_item_bar_chart, this, true);
        markFirst = inflate.findViewById(R.id.mark_first);
        markSecond = inflate.findViewById(R.id.mark_second);
        markThird = inflate.findViewById(R.id.mark_third);
        postValue = findViewById(R.id.post_value);
        getValue = findViewById(R.id.get_value);
        lineEnd = findViewById(R.id.solid_line_end);

    }

    public void setData(int postNumber, @ColorInt int postColor, int getNumber, @ColorInt int getColor) {
        int max = postNumber > getNumber ? postNumber : getNumber;
        max = max + 10 - (max % 10);
        float proportion = calculationProportion(max);
        markFirst.setText("0");
        markThird.setText(String.valueOf(max));
        markSecond.setText(String.valueOf(max / 2));

        postValue.setBackgroundColor(postColor);
        ViewGroup.LayoutParams layoutParams = postValue.getLayoutParams();
        layoutParams.width = (int) (proportion * postNumber);
        postValue.setLayoutParams(layoutParams);

        layoutParams = getValue.getLayoutParams();
        layoutParams.width = (int) (proportion * getNumber);
        getValue.setBackgroundColor(getColor);
    }

    private float calculationProportion(float max) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) lineEnd.getLayoutParams();
        return layoutParams.leftMargin / max;
    }
}