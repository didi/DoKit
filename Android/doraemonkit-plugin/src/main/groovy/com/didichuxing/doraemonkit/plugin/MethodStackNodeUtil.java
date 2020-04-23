package com.didichuxing.doraemonkit.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    public static List<MethodStackNode> firstMethodStackNodes = Collections.synchronizedList(new ArrayList<>());
    public static List<MethodStackNode> secondMethodStackNodes = Collections.synchronizedList(new ArrayList<>());
    public static List<MethodStackNode> thirdMethodStackNodes = Collections.synchronizedList(new ArrayList<>());
    public static List<MethodStackNode> fourthlyMethodStackNodes = Collections.synchronizedList(new ArrayList<>());
    public static List<MethodStackNode> fifthMethodStackNodes = Collections.synchronizedList(new ArrayList<>());

    public static void addFirstLevel(MethodStackNode methodStackNode) {
        if (firstMethodStackNodes != null) {
            firstMethodStackNodes.add(methodStackNode);
        }
    }

    public static void addSecondLevel(MethodStackNode methodStackNode) {
        if (secondMethodStackNodes != null) {
            secondMethodStackNodes.add(methodStackNode);
        }
    }

    public static void addThirdLevel(MethodStackNode methodStackNode) {
        if (thirdMethodStackNodes != null) {
            thirdMethodStackNodes.add(methodStackNode);
        }
    }

    public static void addFourthlyLevel(MethodStackNode methodStackNode) {
        if (fourthlyMethodStackNodes != null) {
            fourthlyMethodStackNodes.add(methodStackNode);
        }
    }

    public static void addFifthLevel(MethodStackNode methodStackNode) {
        if (fifthMethodStackNodes != null) {
            fifthMethodStackNodes.add(methodStackNode);
        }
    }
}
