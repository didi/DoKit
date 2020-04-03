package com.didichuxing.doraemonkit.abridge;

import android.os.Message;

/**
 * [接受远程发送过来的消息]
 * author: yifei
 * created at 18/6/2 下午11:52
 */
public interface AbridgeMessengerCallBack {
    void receiveMessage(Message message);
}
