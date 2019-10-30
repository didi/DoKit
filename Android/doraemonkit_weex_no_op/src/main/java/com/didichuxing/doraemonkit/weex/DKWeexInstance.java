package com.didichuxing.doraemonkit.weex;

import android.app.Application;

/**
 * @author haojianglong
 * @date 2019-06-11
 */
public class DKWeexInstance {

    private DKWeexInstance() {
    }

    public static DKWeexInstance getInstance() {
        return SingleHolder.sInstance;
    }

    private static class SingleHolder {
        private static final DKWeexInstance sInstance = new DKWeexInstance();
    }
    @Deprecated
    public void init(Application app) {

    }

}
