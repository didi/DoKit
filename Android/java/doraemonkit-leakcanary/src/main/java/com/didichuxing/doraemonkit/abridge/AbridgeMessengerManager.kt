package com.didichuxing.doraemonkit.abridge

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import java.util.*
import kotlin.collections.ArrayList

/**
 * 类描述：
 * 创建人：yifei
 * 创建时间：2018/12/18
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
internal class AbridgeMessengerManager private constructor() {
    private lateinit var sApplication: Application
    private lateinit var sServicePkgName: String
    private val sList: MutableList<AbridgeMessengerCallBack> = ArrayList()

    companion object {
        private const val TAG = "AbridgeMessengerManager"
        private const val BIND_SERVICE_ACTION = "android.intent.action.ICALL_MESSENGER_YIFEI"
        private const val BIND_MESSENGER_SERVICE_COMPONENT_NAME_CLS = "com.didichuxing.doraemonkit.abridge.service.MessengerService"
        val instance: AbridgeMessengerManager by lazy { AbridgeMessengerManager() }
    }


    /**
     * 初始化
     *
     * @param sApplication
     * @param sServicePkgName
     */
    fun init(sApplication: Application, sServicePkgName: String) {
        this.sApplication = sApplication
        this.sServicePkgName = sServicePkgName
    }

    fun registerRemoteCallBack(callBack: AbridgeMessengerCallBack) {
        sList.add(callBack)
    }

    fun uRegisterRemoteCallBack(callBack: AbridgeMessengerCallBack?) {
        if (callBack != null) {
            sList.remove(callBack)
        }
    }

    fun callRemote(message: Message) {
        if (sMessenger == null) {
            Log.e(TAG, "error: ipc process not started，please make sure ipc process is alive")
            return
        }
        try {
            message.replyTo = replyMessenger
            sMessenger!!.send(message)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    // TODO: 这里不需要 @SuppressLint("HandlerLeak") 吗
    private val replyMessenger = Messenger(object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            for (callBack in sList) {
                callBack.receiveMessage(msg)
            }
        }
    })
    private var sMessenger: Messenger? = null
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            sMessenger = Messenger(service)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            sMessenger = null
        }
    }

    fun startAndBindService() {
        val serviceIntent = Intent()
        serviceIntent.action = BIND_SERVICE_ACTION
        serviceIntent.component = ComponentName(sServicePkgName, BIND_MESSENGER_SERVICE_COMPONENT_NAME_CLS)
        sApplication.startService(serviceIntent)
        sApplication.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun unBindService() {
        if (sMessenger == null) {
            Log.e(TAG, "error: ipc process not started，please make sure ipc process is alive")
            return
        }
        sApplication.unbindService(serviceConnection)
    }
}