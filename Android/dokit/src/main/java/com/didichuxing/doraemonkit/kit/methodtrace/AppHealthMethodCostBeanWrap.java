package com.didichuxing.doraemonkit.kit.methodtrace;


import java.util.List;

/**
 * Created by hasee on 2017/4/20.
 */
public class AppHealthMethodCostBeanWrap {
    private String title;
    private List<AppHealthMethodCostBean> data;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<AppHealthMethodCostBean> getData() {
        return data;
    }

    public void setData(List<AppHealthMethodCostBean> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AppHealthMethodCostBeanWrap{" +
                "trace='" + title + '\'' +
                ", data=" + data +
                '}';
    }
}
