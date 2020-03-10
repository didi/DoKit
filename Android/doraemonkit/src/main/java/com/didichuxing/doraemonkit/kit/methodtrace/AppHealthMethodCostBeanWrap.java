package com.didichuxing.doraemonkit.kit.methodtrace;


import java.util.List;

/**
 * Created by hasee on 2017/4/20.
 */
public class AppHealthMethodCostBeanWrap {
    String trace;
    List<AppHealthMethodCostBean> data;

    public String getTrace() {
        return trace;
    }

    public void setTrace(String trace) {
        this.trace = trace;
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
                "trace='" + trace + '\'' +
                ", data=" + data +
                '}';
    }
}
