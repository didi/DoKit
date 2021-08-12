package com.didichuxing.doraemonkit.kit.crash;

import java.io.Serializable;

/**
 * Created by wanglikun on 2019-06-12
 */
public class CrashInfo implements Serializable {
    public final Throwable tr;

    public final long time;

    public CrashInfo(Throwable tr, long l) {
        this.tr = tr;
        this.time = l;
    }
}