package com.didichuxing.doraemonkit.kit.sysinfo;

/**
 * Created by wanglikun on 2018/9/14.
 */

public class SysInfoItem {
    public boolean isPermission;
    public final String name;
    public final String value;


    public SysInfoItem(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public SysInfoItem(String name, String value, boolean isPermission) {
        this.name = name;
        this.value = value;
        this.isPermission = isPermission;
    }
}
