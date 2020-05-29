package com.didichuxing.doraemonkit.kit.methodtrace;


/**
 * Created by hasee on 2017/4/20.
 */
public class AppHealthMethodCostBean {
    private String functionName;
    private String costTime = "0";


    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getCostTime() {
        return costTime;
    }

    public void setCostTime(String costTime) {
        this.costTime = costTime;
    }


    @Override
    public String toString() {
        return "AppHealthMethodCostBean{" +
                "functionName='" + functionName + '\'' +
                ", costTime='" + costTime + '\'' +
                '}';
    }
}
