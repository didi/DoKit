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
    private int Level = 0;
    private int childSize = 0;
    private String className;
    private String methodName;
    private List<MethodStackNode> children;

    public int getLevel() {
        return Level;
    }

    public void setLevel(int level) {
        Level = level;
    }

    public int getChildSize() {
        if (children == null) {
            return 0;
        }
        return children.size();
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

    public List<MethodStackNode> getChildren() {
        return children;
    }

    public void setChildren(List<MethodStackNode> children) {
        this.children = children;
    }
}
