package com.didichuxing.doraemonkit.aop.method_stack;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/23-11:21
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class MethodInvokNode {
    private MethodInvokNode parent;
    private long startTimeMillis;
    private long endTimeMillis;
    private int costTimeMillis;
    private String currentThreadName;
    private String className;
    private String methodName;
    private int level;
    private List<MethodInvokNode> children = new ArrayList<>();

    public MethodInvokNode getParent() {
        return parent;
    }

    public void setParent(MethodInvokNode parent) {
        this.parent = parent;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public void setStartTimeMillis(long startTimeMillis) {
        this.startTimeMillis = startTimeMillis;
    }

    public long getEndTimeMillis() {
        return endTimeMillis;
    }

    public void setEndTimeMillis(long endTimeMillis) {
        this.endTimeMillis = endTimeMillis;
        this.costTimeMillis = (int) (endTimeMillis - startTimeMillis);
    }

    public int getCostTimeMillis() {
        return (int) (endTimeMillis - startTimeMillis);
    }


    public String getCurrentThreadName() {
        return currentThreadName;
    }

    public void setCurrentThreadName(String currentThreadName) {
        this.currentThreadName = currentThreadName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void addChild(MethodInvokNode methodInvokNode) {
        if (children != null) {
            children.add(methodInvokNode);
        }
    }

    public void setCostTimeMillis(int costTimeMillis) {
        this.costTimeMillis = costTimeMillis;
    }

    public List<MethodInvokNode> getChildren() {
        return children;
    }

    public void setChildren(List<MethodInvokNode> children) {
        this.children = children;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
