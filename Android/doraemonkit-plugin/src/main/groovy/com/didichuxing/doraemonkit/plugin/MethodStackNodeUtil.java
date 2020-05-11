package com.didichuxing.doraemonkit.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/22-14:33
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class MethodStackNodeUtil {
    public static final int LEVEL_0 = 0;
    public static final int LEVEL_1 = 1;
    public static final int LEVEL_2 = 2;
    public static final int LEVEL_3 = 3;
    public static final int LEVEL_4 = 4;
    /**
     * 最顶层的MethodStackNodes
     */
    public static Map<String, MethodStackNode> firstMethodStackNodes = Collections.synchronizedMap(new HashMap<>());
    public static Map<String, MethodStackNode> secondMethodStackNodes = Collections.synchronizedMap(new HashMap<>());
    public static Map<String, MethodStackNode> thirdMethodStackNodes = Collections.synchronizedMap(new HashMap<>());
    public static Map<String, MethodStackNode> fourthlyMethodStackNodes = Collections.synchronizedMap(new HashMap<>());
    public static Map<String, MethodStackNode> fifthMethodStackNodes = Collections.synchronizedMap(new HashMap<>());

    public static void addFirstLevel(MethodStackNode methodStackNode) {
        if (firstMethodStackNodes != null) {
            firstMethodStackNodes.put(methodStackNode.getClassName() + "&" + methodStackNode.getMethodName() + "&" + methodStackNode.getDesc(), methodStackNode);
        }
    }

    public static void addSecondLevel(MethodStackNode methodStackNode) {
        if (secondMethodStackNodes != null) {
            secondMethodStackNodes.put(methodStackNode.getClassName() + "&" + methodStackNode.getMethodName() + "&" + methodStackNode.getDesc(), methodStackNode);
        }
    }

    public static void addThirdLevel(MethodStackNode methodStackNode) {
        if (thirdMethodStackNodes != null) {
            thirdMethodStackNodes.put(methodStackNode.getClassName() + "&" + methodStackNode.getMethodName() + "&" + methodStackNode.getDesc(), methodStackNode);
        }
    }

    public static void addFourthlyLevel(MethodStackNode methodStackNode) {
        if (fourthlyMethodStackNodes != null) {
            fourthlyMethodStackNodes.put(methodStackNode.getClassName() + "&" + methodStackNode.getMethodName() + "&" + methodStackNode.getDesc(), methodStackNode);
        }
    }

    public static void addFifthLevel(MethodStackNode methodStackNode) {
        if (fifthMethodStackNodes != null) {
            fifthMethodStackNodes.put(methodStackNode.getClassName() + "&" + methodStackNode.getMethodName() + "&" + methodStackNode.getDesc(), methodStackNode);
        }
    }
}
