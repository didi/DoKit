package com.didichuxing.doraemonkit.plugin.platformhttp;

import com.quinn.hunter.transform.RunVariant;

/**
 * Created by Quinn on 07/10/2018.
 */
public class PlatformHttpExtension {

    public RunVariant runVariant = RunVariant.DEBUG;
    public boolean weaveEventListener = true;
    public boolean duplcatedClassSafeMode = false;

    @Override
    public String toString() {
        return "OkHttpHunterExtension{" +
                "runVariant=" + runVariant +
                ", weaveEventListener=" + weaveEventListener +
                ", duplcatedClassSafeMode=" + duplcatedClassSafeMode +
                '}';
    }

}
