package com.didichuxing.doraemonkit.plugin;

import com.quinn.hunter.transform.RunVariant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Quinn on 07/10/2018.
 */
public class DokitExtension {

    public RunVariant runVariant = RunVariant.DEBUG;
    public boolean duplcatedClassSafeMode = false;
    /**
     * dokit 插件开关
     */
    public boolean dokitPluginSwitch = true;
    /**
     * 慢函数开关
     */
    public boolean slowMethodSwitch = true;
    /**
     * 单位为us 默认200us
     */
    public int thresholdTime = 500;
    public List<String> packageNames = new ArrayList<>();


    @Override
    public String toString() {
        return "FlagExtension{" +
                "runVariant=" + runVariant +
                ", duplcatedClassSafeMode=" + duplcatedClassSafeMode +
                '}';
    }

}
