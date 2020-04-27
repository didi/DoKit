package com.didichuxing.doraemonkit.aop.method_stack;

import android.util.Log;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.didichuxing.doraemonkit.aop.MethodCostUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/22-15:44
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class MethodStackUtil {
    /**
     * key className&methodName
     */
    public static ConcurrentHashMap<String, MethodInvokNode> ROOT_METHOD_STACKS = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, MethodInvokNode> LEVEL1_METHOD_STACKS = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, MethodInvokNode> LEVEL2_METHOD_STACKS = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, MethodInvokNode> LEVEL3_METHOD_STACKS = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, MethodInvokNode> LEVEL4_METHOD_STACKS = new ConcurrentHashMap<>();
    private static final String TAG = "MethodStackUtil";


    /**
     * 静态内部类单例
     */
    private static class Holder {
        private static MethodStackUtil INSTANCE = new MethodStackUtil();
    }

    public static MethodStackUtil getInstance() {
        return MethodStackUtil.Holder.INSTANCE;
    }

    /**
     * @param level
     * @param methodName
     * @param classObj   null 代表静态函数
     */
    public void recodeObjectMethodCostStart(int level, String className, String methodName, String desc, Object classObj) {

        try {
            MethodInvokNode methodInvokNode = new MethodInvokNode();
            methodInvokNode.setStartTimeMillis(System.currentTimeMillis());
            methodInvokNode.setCurrentThreadName(Thread.currentThread().getName());
            methodInvokNode.setClassName(className);
            methodInvokNode.setMethodName(methodName);

            if (level == 0) {
                methodInvokNode.setLevel(0);
                ROOT_METHOD_STACKS.put(String.format("%s&%s", className, methodName), methodInvokNode);
            } else if (level == 1) {
                methodInvokNode.setLevel(1);
                LEVEL1_METHOD_STACKS.put(String.format("%s&%s", className, methodName), methodInvokNode);
            } else if (level == 2) {
                methodInvokNode.setLevel(2);
                LEVEL2_METHOD_STACKS.put(String.format("%s&%s", className, methodName), methodInvokNode);
            } else if (level == 3) {
                methodInvokNode.setLevel(3);
                LEVEL3_METHOD_STACKS.put(String.format("%s&%s", className, methodName), methodInvokNode);
            } else if (level == 4) {
                methodInvokNode.setLevel(4);
                LEVEL4_METHOD_STACKS.put(String.format("%s&%s", className, methodName), methodInvokNode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param level
     * @param className
     * @param methodName
     * @param desc
     * @param classObj   null 代表静态函数
     */
    public void recodeObjectMethodCostEnd(int level, String className, String methodName, String desc, Object classObj) {

        synchronized (MethodCostUtil.class) {
            try {
                MethodInvokNode methodInvokNode = null;

                if (level == 0) {
                    methodInvokNode = ROOT_METHOD_STACKS.get(String.format("%s&%s", className, methodName));
                } else if (level == 1) {
                    methodInvokNode = LEVEL1_METHOD_STACKS.get(String.format("%s&%s", className, methodName));
                } else if (level == 2) {
                    methodInvokNode = LEVEL2_METHOD_STACKS.get(String.format("%s&%s", className, methodName));
                } else if (level == 3) {
                    methodInvokNode = LEVEL3_METHOD_STACKS.get(String.format("%s&%s", className, methodName));
                } else if (level == 4) {
                    methodInvokNode = LEVEL4_METHOD_STACKS.get(String.format("%s&%s", className, methodName));
                }
                if (methodInvokNode != null) {
                    methodInvokNode.setEndTimeMillis(System.currentTimeMillis());
                    bindNode(level, methodInvokNode);
                }

                //打印函数调用栈
                if (level == 0) {
                    if (methodInvokNode != null) {
                        toStack(methodInvokNode);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    private String getParentMethod(String currentClassName, String currentMethodName) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        int index = 0;
        for (int i = 0; i < stackTraceElements.length; i++) {
            StackTraceElement stackTraceElement = stackTraceElements[i];
            if (currentClassName.equals(stackTraceElement.getClassName().replaceAll("\\.", "/")) && currentMethodName.equals(stackTraceElement.getMethodName())) {
                index = i;
                break;
            }
        }
        StackTraceElement parentStackTraceElement = stackTraceElements[index + 1];

        return String.format("%s&%s", parentStackTraceElement.getClassName().replaceAll("\\.", "/"), parentStackTraceElement.getMethodName());
    }


    private void bindNode(int level, MethodInvokNode methodInvokNode) {
        if (methodInvokNode == null) {
            return;
        }

        //过滤掉小于10ms的函数
        if (methodInvokNode.getCostTimeMillis() <= 1) {
            return;
        }

        MethodInvokNode parentMethodNode;
        switch (level) {
            case 1:
                //设置父node 并将自己添加到父node中
                parentMethodNode = ROOT_METHOD_STACKS.get(getParentMethod(methodInvokNode.getClassName(), methodInvokNode.getMethodName()));
                if (parentMethodNode != null) {
                    methodInvokNode.setParent(parentMethodNode);
                    parentMethodNode.addChild(methodInvokNode);
                }

                break;
            case 2:
                //设置父node 并将自己添加到父node中
                parentMethodNode = LEVEL1_METHOD_STACKS.get(getParentMethod(methodInvokNode.getClassName(), methodInvokNode.getMethodName()));
                if (parentMethodNode != null) {
                    methodInvokNode.setParent(parentMethodNode);
                    parentMethodNode.addChild(methodInvokNode);
                }
                break;
            case 3:
                //设置父node 并将自己添加到父node中
                parentMethodNode = LEVEL2_METHOD_STACKS.get(getParentMethod(methodInvokNode.getClassName(), methodInvokNode.getMethodName()));
                if (parentMethodNode != null) {
                    methodInvokNode.setParent(parentMethodNode);
                    parentMethodNode.addChild(methodInvokNode);
                }
                break;
            case 4:
                //设置父node 并将自己添加到父node中
                parentMethodNode = LEVEL3_METHOD_STACKS.get(getParentMethod(methodInvokNode.getClassName(), methodInvokNode.getMethodName()));
                if (parentMethodNode != null) {
                    methodInvokNode.setParent(parentMethodNode);
                    parentMethodNode.addChild(methodInvokNode);
                }
                break;

            default:
                break;
        }
    }


    public void recodeStaticMethodCostStart(int level, String className, String methodName, String desc) {
        recodeObjectMethodCostStart(level, className, methodName, desc, null);
    }


    public void recodeStaticMethodCostEnd(int level, String className, String methodName, String desc) {
        recodeObjectMethodCostEnd(level, className, methodName, desc, null);
    }

    private void jsonTravel(List<MethodStackBean> methodStackBeans, List<MethodInvokNode> methodInvokNodes) {
        if (methodInvokNodes == null) {
            return;
        }
        for (MethodInvokNode methodInvokNode : methodInvokNodes) {
            MethodStackBean methodStackBean = new MethodStackBean();
            methodStackBean.setCostTime(methodInvokNode.getCostTimeMillis());
            methodStackBean.setFunction(methodInvokNode.getClassName() + "&" + methodInvokNode.getMethodName());
            methodStackBean.setChildren(new ArrayList<MethodStackBean>());
            jsonTravel(methodStackBean.getChildren(), methodInvokNode.getChildren());
            methodStackBeans.add(methodStackBean);
        }
    }


    private void stackTravel(StringBuilder stringBuilder, List<MethodInvokNode> methodInvokNodes) {
        if (methodInvokNodes == null) {
            return;
        }
        for (MethodInvokNode methodInvokNode : methodInvokNodes) {
            stringBuilder.append(String.format("%s%s%s%s%s", methodInvokNode.getLevel(), SPACE_0, methodInvokNode.getCostTimeMillis() + "ms", getSpaceString(methodInvokNode.getLevel()), methodInvokNode.getClassName() + "&" + methodInvokNode.getMethodName())).append("\n");
            stackTravel(stringBuilder, methodInvokNode.getChildren());
        }
    }

    public void toJson() {
        List<MethodStackBean> methodStackBeans = new ArrayList<>();
        for (MethodInvokNode methodInvokNode : ROOT_METHOD_STACKS.values()) {
            MethodStackBean methodStackBean = new MethodStackBean();
            methodStackBean.setCostTime(methodInvokNode.getCostTimeMillis());
            methodStackBean.setFunction(methodInvokNode.getClassName() + "&" + methodInvokNode.getMethodName());
            methodStackBean.setChildren(new ArrayList<MethodStackBean>());
            jsonTravel(methodStackBean.getChildren(), methodInvokNode.getChildren());
            methodStackBeans.add(methodStackBean);
        }
        String json = GsonUtils.toJson(methodStackBeans);
        LogUtils.json(json);
    }

    private static final String SPACE_0 = "********";
    private static final String SPACE_1 = "*************";
    private static final String SPACE_2 = "*****************";
    private static final String SPACE_3 = "*********************";
    private static final String SPACE_4 = "*************************";

    public void toStack(MethodInvokNode methodInvokNode) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("=========DoKit函数调用栈==========").append("\n");
        stringBuilder.append(String.format("%s    %s    %s", "level", "time", "function")).append("\n");
        stringBuilder.append(String.format("%s%s%s%s%s", methodInvokNode.getLevel(), SPACE_0, methodInvokNode.getCostTimeMillis() + "ms", getSpaceString(methodInvokNode.getLevel()), methodInvokNode.getClassName() + "&" + methodInvokNode.getMethodName())).append("\n");
        stackTravel(stringBuilder, methodInvokNode.getChildren());

        Log.i(TAG, stringBuilder.toString());
    }


    private String getSpaceString(int level) {
        if (level == 0) {
            return SPACE_0;
        } else if (level == 1) {
            return SPACE_1;
        } else if (level == 2) {
            return SPACE_2;
        } else if (level == 3) {
            return SPACE_3;
        } else if (level == 4) {
            return SPACE_4;
        }
        return SPACE_0;
    }
}
