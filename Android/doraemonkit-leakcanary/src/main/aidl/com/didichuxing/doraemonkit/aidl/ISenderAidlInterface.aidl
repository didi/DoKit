// ICall.aidl
package com.didichuxing.doraemonkit.aidl;

import  com.didichuxing.doraemonkit.aidl.IReceiverAidlInterface;

interface ISenderAidlInterface {

    void join(IBinder token);

    void leave(IBinder token);

    void sendMessage(String json);

    void registerCallback(IReceiverAidlInterface cb);

    void unregisterCallback(IReceiverAidlInterface cb);
}
