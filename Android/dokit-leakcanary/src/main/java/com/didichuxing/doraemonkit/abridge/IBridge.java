package com.didichuxing.doraemonkit.abridge;

import android.app.Application;
import android.os.Message;

import androidx.annotation.NonNull;

/**
 * 类描述：
 * 创建人：yifei
 * 创建时间：2018/5/15
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public final class IBridge {

    public enum AbridgeType {
        AIDL, MESSENGER
    }

    private IBridge() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void init(@NonNull final Application app, String servicePkgName, AbridgeType type) {
        if (type == AbridgeType.AIDL) {
            AbridgeManager.getInstance().init(app, servicePkgName);
            AbridgeManager.getInstance().startAndBindService();
        } else {
            AbridgeMessengerManager.getInstance().init(app, servicePkgName);
            AbridgeMessengerManager.getInstance().startAndBindService();
        }
    }

    public static void recycle() {
        AbridgeManager.getInstance().unBindService();
        AbridgeMessengerManager.getInstance().unBindService();
    }

    public static void sendAIDLMessage(String message) {
        AbridgeManager.getInstance().callRemote(message);
    }

    public static void registerAIDLCallBack(AbridgeCallBack callBack) {
        AbridgeManager.getInstance().registerRemoteCallBack(callBack);
    }

    public static void uRegisterAIDLCallBack(AbridgeCallBack callBack) {
        AbridgeManager.getInstance().uRegisterRemoteCallBack(callBack);
    }

    public static void sendMessengerMessage(Message message) {
        AbridgeMessengerManager.getInstance().callRemote(message);
    }

    public static void registerMessengerCallBack(AbridgeMessengerCallBack callBack) {
        AbridgeMessengerManager.getInstance().registerRemoteCallBack(callBack);
    }

    public static void uRegisterMessengerCallBack(AbridgeMessengerCallBack callBack) {
        AbridgeMessengerManager.getInstance().uRegisterRemoteCallBack(callBack);
    }

}
