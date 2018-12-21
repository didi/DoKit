package com.didichuxing.doraemonkit.adb;

/**
 * Created by yangmenglin on 2018/11/1.
 */

public interface Callback {
    void onSuccess(String adbResponse);
    void onFail(String failString);
}
