package com.didichuxing.doraemonkit.util.netstate;

import android.content.Context;

/**
 * @author maple on 2019/7/9 17:58.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public interface INetwork {
    void  register(NetChangeObserver observer, Context context);
    void unRegister(Context context);
}
