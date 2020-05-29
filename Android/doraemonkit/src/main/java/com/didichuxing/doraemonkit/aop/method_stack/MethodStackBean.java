package com.didichuxing.doraemonkit.aop.method_stack;

import java.util.List;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/23-17:42
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class MethodStackBean {
    String function;
    String costTime;
    List<MethodStackBean> children;

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getCostTime() {
        return costTime;
    }

    public void setCostTime(int costTime) {
        this.costTime = costTime + "ms";
    }

    public List<MethodStackBean> getChildren() {
        return children;
    }

    public void setChildren(List<MethodStackBean> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "MethodStackBean{" +
                "function='" + function + '\'' +
                ", costTime=" + costTime +
                ", children=" + children +
                '}';
    }
}
