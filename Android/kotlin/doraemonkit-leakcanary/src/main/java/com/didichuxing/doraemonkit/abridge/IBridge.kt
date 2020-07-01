package com.didichuxing.doraemonkit.abridge

import android.app.Application
import android.os.Message

/**
 * 类描述：
 * 创建人：yifei
 * 创建时间：2018/5/15
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
class IBridge private constructor() {
    enum class AbridgeType {
        AIDL, MESSENGER
    }

    companion object {
        fun init(app: Application, servicePkgName: String?, type: AbridgeType) {
            if (type == AbridgeType.AIDL) {
                AbridgeManager.instance.init(app, servicePkgName!!)
                AbridgeManager.instance.startAndBindService()
            } else {
                AbridgeMessengerManager.instance.init(app, servicePkgName!!)
                AbridgeMessengerManager.instance.startAndBindService()
            }
        }

        fun recycle() {
            AbridgeManager.instance.unBindService()
            AbridgeMessengerManager.instance.unBindService()
        }

        fun sendAIDLMessage(message: String?) {
            AbridgeManager.instance.callRemote(message)
        }

        fun registerAIDLCallBack(callBack: AbridgeCallBack?) {
            AbridgeManager.instance.registerRemoteCallBack(callBack!!)
        }

        fun uRegisterAIDLCallBack(callBack: AbridgeCallBack?) {
            AbridgeManager.instance.uRegisterRemoteCallBack(callBack)
        }

        fun sendMessengerMessage(message: Message?) {
            AbridgeMessengerManager.instance.callRemote(message!!)
        }

        fun registerMessengerCallBack(callBack: AbridgeMessengerCallBack?) {
            AbridgeMessengerManager.instance.registerRemoteCallBack(callBack!!)
        }

        fun uRegisterMessengerCallBack(callBack: AbridgeMessengerCallBack?) {
            AbridgeMessengerManager.instance.uRegisterRemoteCallBack(callBack)
        }
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}