package com.didichuxing.doraemonkit.view.bravh.listener;

import androidx.annotation.Nullable;

import com.didichuxing.doraemonkit.view.bravh.listener.OnUpFetchListener;

/**
 * @author: limuyang
 *  2019-12-03
 * @Description: UpFetch需要设置的接口。使用java定义，以兼容java写法
 */
public interface UpFetchListenerImp {
    void setOnUpFetchListener(@Nullable OnUpFetchListener listener);
}
