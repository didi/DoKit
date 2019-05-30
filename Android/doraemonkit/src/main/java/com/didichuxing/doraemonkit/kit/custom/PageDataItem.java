package com.didichuxing.doraemonkit.kit.custom;

import android.support.annotation.StringRes;
import android.view.View;

public class PageDataItem {
    public String pageName;

    public PageDataItemChild<Double> upNetWork;
    public PageDataItemChild<Double> downNetWork;
    public PageDataItemChild<Float> memory;
    public PageDataItemChild<Float> cpu;
    public PageDataItemChild<Integer> fps;
    public PageDataItem() {
    }
}

class PageDataItemChild<T extends Number> {
    @StringRes
    public int nameResId;
    public T min;
    public T max;
    public T avg;

    public PageDataItemChild(int nameResId) {
        this.nameResId = nameResId;
    }

    public int getVisibility(PageDataItemChild<? extends  Number> child){
        return 0 <  getValue(child.min)+ getValue(child.max) + getValue(child.avg)
                ? View.VISIBLE: View.GONE;
    }

    private double getValue(Number data){
        return null == data? 0: data.doubleValue();
    }

    public int getNameResId() {
        return nameResId;
    }

    public void setNameResId(int nameResId) {
        this.nameResId = nameResId;
    }

    public void setMin(T min) {
        this.min = min;
    }

    public T getMax() {
        return max;
    }

    public void setMax(T max) {
        this.max = max;
    }

    public T getAvg() {
        return avg;
    }

    public void setAvg(T avg) {
        this.avg = avg;
    }
}
