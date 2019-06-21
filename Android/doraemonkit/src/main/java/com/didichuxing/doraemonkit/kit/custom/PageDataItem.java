package com.didichuxing.doraemonkit.kit.custom;

import android.support.annotation.StringRes;
import android.view.View;

public class PageDataItem {
    public String pageName;

    public PageDataItemChild upNetWork;
    public PageDataItemChild downNetWork;
    public PageDataItemChild memory;
    public PageDataItemChild cpu;
    public PageDataItemChild fps;
    public PageDataItem() {
    }
}

class PageDataItemChild {
    @StringRes
    public int nameResId;
    public double min;
    public double max;
    public double avg;

    public PageDataItemChild(int nameResId) {
        this.nameResId = nameResId;
    }

    public int getVisibility(PageDataItemChild child){
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

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getAvg() {
        return avg;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }
}
