package com.didichuxing.doraemonkit.plugin.amap;

import com.quinn.hunter.transform.RunVariant;

/**
 * Created by Quinn on 07/10/2018.
 */
public class AmapExtension {

    public RunVariant runVariant = RunVariant.DEBUG;
    public boolean duplcatedClassSafeMode = false;

    @Override
    public String toString() {
        return "FlagExtension{" +
                "runVariant=" + runVariant +
                ", duplcatedClassSafeMode=" + duplcatedClassSafeMode +
                '}';
    }

}
