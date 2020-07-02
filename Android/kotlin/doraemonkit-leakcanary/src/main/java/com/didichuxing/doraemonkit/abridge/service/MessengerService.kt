package com.didichuxing.doraemonkit.abridge.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.*

class MessengerService: Service() {
    companion object {
        private const val TAG = "MessengerService"
    }

    private val messengers: ArrayList<Messenger> = ArrayList()

    @SuppressLint("HandlerLeak")
    private val messenger = Messenger(
            object : Handler() {
                override fun handleMessage(msg: Message) {
                    super.handleMessage(msg)
                    if (msg.arg1 == 0x0000c1) { //退出接受
                        val str = msg.data["MessengerService"] as String?
                        if (!str.isNullOrEmpty() && str.equals("unregisterCallback", ignoreCase = true)) {
                            messengers.remove(msg.replyTo)
                            return
                        }
                    }
                    if (!messengers.contains(msg.replyTo)) {
                        messengers.add(msg.replyTo)
                    }
                    try {
                        for (reply in messengers) {
                            reply.send(msg)
                        }
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }
                }
            })

    override fun onBind(intent: Intent?): IBinder = messenger.binder
}