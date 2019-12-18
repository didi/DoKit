package com.didichuxing.doraemonkit.plugin.debug_flag;

import com.quinn.hunter.transform.RunVariant;

/**
 * Created by Quinn on 07/10/2018.
 */
public class FlagExtension {

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
