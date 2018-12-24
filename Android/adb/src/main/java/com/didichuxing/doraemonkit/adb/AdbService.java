package com.didichuxing.doraemonkit.adb;

import android.content.Context;
import android.text.TextUtils;

/**
 * Created by yangmenglin on 2018/11/1.
 */

public class AdbService {
    private ThreadPoolProxy mProxy;
    private AdbConnector mAdbConnector;
    private Context mContext;
    public AdbService(Context context){
        mProxy = ThreadPoolProxyFactory.getThreadPoolProxy();
        mAdbConnector = new AdbConnector();
        mContext = context;
    }
    public void performAdbRequest(final String cmd, final Callback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String response = mAdbConnector.openShell(mContext, cmd);
                    if (!TextUtils.isEmpty(response)) {
                        if (callback != null) {
                            callback.onSuccess(response);
                        }
                    } else {
                        if (callback != null) {
                            callback.onFail("");
                        }
                    }
                } catch (Exception e) {
                    if (callback != null) {
                        callback.onFail(e.getMessage());
                    }
                }
            }
        };
        mProxy.execute(runnable);
    }


}
