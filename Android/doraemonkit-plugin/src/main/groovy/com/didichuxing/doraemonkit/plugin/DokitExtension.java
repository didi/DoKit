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
    public boolean slowMethodSwitch = true;
    /**
     * 单位为500us
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
