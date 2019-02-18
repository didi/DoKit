package com.didichuxing.doraemonkit.config;

/**
 * Created by wanglikun on 2019/1/7
 */
public class LayoutBorderConfig {
    private static boolean sLayoutBorderOpen;
    private static boolean sLayoutLevelOpen;

    public static boolean isLayoutBorderOpen() {
        return sLayoutBorderOpen;
    }

    public static void setLayoutBorderOpen(boolean open) {
        sLayoutBorderOpen = open;
    }

    public static boolean isLayoutLevelOpen() {
        return sLayoutLevelOpen;
    }

    public static void setLayoutLevelOpen(boolean open) {
        sLayoutLevelOpen = open;
    }
}