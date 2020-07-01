package com.didichuxing.doraemonkit.abridge

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.RemoteException
import android.text.TextUtils
import android.util.Log
import com.didichuxing.doraemonkit.aidl.IReceiverAidlInterface
import com.didichuxing.doraemonkit.aidl.ISenderAidlInterface
import kotlin.collections.ArrayList

/**
 * 类描述：
 * 创建人：yifei
 * 创建时间：2018/12/18
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
internal class AbridgeManager private constructor() {
    private lateinit var sApplication: Application
    private lateinit var sServicePkgName: String
    private lateinit var sHandler: Handler
    private val sList: MutableList<AbridgeCallBack> = ArrayList()

    companion object {
        private const val TAG = "AbridgeManager"
        private const val BIND_SERVICE_ACTION = "android.intent.action.ICALL_AIDL_YIFEI"
        private const val BIND_SERVICE_COMPONENT_NAME_CLS = "com.didichuxing.doraemonkit.abridge.service.ABridgeService"

        val instance: AbridgeManager by lazy {
            AbridgeManager()
        }
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
        sHandler = Handler(sApplication.mainLooper)
    }

    fun registerRemoteCallBack(callBack: AbridgeCallBack) {
        sList.add(callBack)
    }

    fun uRegisterRemoteCallBack(callBack: AbridgeCallBack?) {
        if (callBack != null) {
            sList.remove(callBack)
        }
    }

    fun callRemote(message: String?) {
        if (iSenderAidlInterface == null) {
            Log.e(TAG, "error: ipc process not started，please make sure ipc process is alive")
            return
        }
        if (!TextUtils.isEmpty(message)) {
            try {
                iSenderAidlInterface?.sendMessage(message)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    private val sBinder: IBinder = Binder()
    private var iSenderAidlInterface: ISenderAidlInterface? = null
    private val iReceiverAidlInterface: IReceiverAidlInterface = object : IReceiverAidlInterface.Stub() {
        @Throws(RemoteException::class)
        override fun receiveMessage(json: String?) {
            sHandler.post {
                for (medium in sList) {
                    medium.receiveMessage(json)
                }
            }
        }
    }
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            iSenderAidlInterface = ISenderAidlInterface.Stub.asInterface(iBinder)
            if (iSenderAidlInterface == null) {
                Log.e(TAG, "error: ipc process not started，please make sure ipc process is alive")
                return
            }
            try {
                iSenderAidlInterface?.join(sBinder)
                iSenderAidlInterface?.registerCallback(iReceiverAidlInterface)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            iSenderAidlInterface = null
        }
    }

    /**
     * 启动服务
     */
    fun startAndBindService() {
        val serviceIntent = Intent()
        serviceIntent.action = BIND_SERVICE_ACTION
        serviceIntent.component = ComponentName(sServicePkgName, BIND_SERVICE_COMPONENT_NAME_CLS)
        sApplication.startService(serviceIntent)
        sApplication.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    /**
     * 关闭服务
     */
    fun unBindService() {
        if (iSenderAidlInterface == null) {
            Log.e(TAG, "error: ipc process not started，please make sure ipc process is alive")
            return
        }
        try {
            iSenderAidlInterface?.unregisterCallback(iReceiverAidlInterface)
            iSenderAidlInterface?.leave(sBinder)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        sApplication.unbindService(serviceConnection)
    }
}