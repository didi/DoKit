package com.didichuxing.doraemonkit.weex;

import android.app.Application;

import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.kit.IKit;
import com.didichuxing.doraemonkit.weex.log.WeexLogKit;
import com.didichuxing.doraemonkit.weex.devtool.DevToolKit;
import com.didichuxing.doraemonkit.weex.info.WeexInfoKit;
import com.didichuxing.doraemonkit.weex.storage.StorageKit;

import java.util.ArrayList;
import java.util.List;

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
        List<IKit> bizKits = new ArrayList<>();
        bizKits.add(new WeexLogKit());
        bizKits.add(new StorageKit());
        bizKits.add(new WeexInfoKit());
        bizKits.add(new DevToolKit());
        DoraemonKit.install(app, bizKits);
    }

}
