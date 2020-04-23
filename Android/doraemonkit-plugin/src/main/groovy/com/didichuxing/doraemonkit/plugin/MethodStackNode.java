package com.didichuxing.doraemonkit.plugin;

import java.util.List;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/21-20:50
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class MethodStackNode {
    private int level = 0;
    private String className;
    private String methodName;
    private String desc;
    private String parentDesc;
    private String parentClassName;
    private String parentMethodName;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getParentDesc() {
        return parentDesc;
    }

    public void setParentDesc(String parentDesc) {
        this.parentDesc = parentDesc;
    }

    public String getParentClassName() {
        return parentClassName;
    }

    public void setParentClassName(String parentClassName) {
        this.parentClassName = parentClassName;
    }

    public String getParentMethodName() {
        return parentMethodName;
    }

    public void setParentMethodName(String parentMethodName) {
        this.parentMethodName = parentMethodName;
    }
}
