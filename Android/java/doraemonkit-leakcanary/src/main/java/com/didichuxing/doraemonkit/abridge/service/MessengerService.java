package com.didichuxing.doraemonkit.abridge.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class MessengerService extends Service {
    public final static String TAG = "MessengerService";

    private List<Messenger> messengers = new ArrayList<>();

    @SuppressLint("HandlerLeak")
    private Messenger messenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == 0x0000c1) {//退出接受
                String str = (String) msg.getData().get("MessengerService");
                if (!TextUtils.isEmpty(str) && str.equalsIgnoreCase("unregisterCallback")) {
                    messengers.remove(msg.replyTo);
                    return;
                }
            }
            if (!messengers.contains(msg.replyTo)) {
                messengers.add(msg.replyTo);
            }
            try {
                for (Messenger reply : messengers) {
                    reply.send(msg);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    });

    public MessengerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }
}
