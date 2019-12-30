package com.didichuxing.doraemonkit.util.netstate;

/**
 * @author maple on 2019/7/9 15:09.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public interface NetChangeObserver {
    void  onConnect(NetType netType);
    void  onDisConnect();
}
