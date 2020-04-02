package com.didichuxing.doraemonkit.abridge;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 * 创建人：yifei
 * 创建时间：2018/12/18
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
final class AbridgeMessengerManager {
    private static final String TAG = "AbridgeMessengerManager";
    private static final String BIND_SERVICE_ACTION = "android.intent.action.ICALL_MESSENGER_YIFEI";
    private static final String BIND_MESSENGER_SERVICE_COMPONENT_NAME_CLS = "com.didichuxing.doraemonkit.abridge.service.MessengerService";
    private static AbridgeMessengerManager instance;

    private Application sApplication;
    private String sServicePkgName;
    private List<AbridgeMessengerCallBack> sList;

    private AbridgeMessengerManager() {
        sList = new ArrayList<>();
    }

    public static AbridgeMessengerManager getInstance() {
        if (instance == null) {
            synchronized (AbridgeMessengerManager.class) {
                if (instance == null) {
                    instance = new AbridgeMessengerManager();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化
     *
     * @param sApplication
     * @param sServicePkgName
     */
    public void init(Application sApplication, String sServicePkgName) {
        this.sApplication = sApplication;
        this.sServicePkgName = sServicePkgName;
    }

    public void registerRemoteCallBack(AbridgeMessengerCallBack callBack) {
        if (sList != null) {
            sList.add(callBack);
        }
    }

    public void uRegisterRemoteCallBack(AbridgeMessengerCallBack callBack) {
        if (sList != null && callBack != null) {
            sList.remove(callBack);
        }
    }

    public void callRemote(Message message) {
        if (sMessenger == null) {
            Log.e(TAG, "error: ipc process not started，please make sure ipc process is alive");
            return;
        }
        try {
            message.replyTo = replyMessenger;
            sMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private Messenger replyMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            for (AbridgeMessengerCallBack callBack : sList) {
                callBack.receiveMessage(msg);
            }
        }
    });
    private Messenger sMessenger;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            sMessenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            sMessenger = null;
        }
    };

    public void startAndBindService() {
        Intent serviceIntent = new Intent();
        serviceIntent.setAction(BIND_SERVICE_ACTION);
        serviceIntent.setComponent(new ComponentName(sServicePkgName, BIND_MESSENGER_SERVICE_COMPONENT_NAME_CLS));
        sApplication.startService(serviceIntent);
        sApplication.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unBindService() {
        if (sMessenger == null) {
            Log.e(TAG, "error: ipc process not started，please make sure ipc process is alive");
            return;
        }
        sApplication.unbindService(serviceConnection);
    }

}
