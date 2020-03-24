// ICall.aidl
package com.sjtu.yifei.aidl;

import com.sjtu.yifei.aidl.IReceiverAidlInterface;

interface ISenderAidlInterface {

    void join(IBinder token);

    void leave(IBinder token);

    void sendMessage(String json);

    void registerCallback(IReceiverAidlInterface cb);

    void unregisterCallback(IReceiverAidlInterface cb);
}
