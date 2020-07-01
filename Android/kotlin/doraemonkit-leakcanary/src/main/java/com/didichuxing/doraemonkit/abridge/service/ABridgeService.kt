package com.didichuxing.doraemonkit.abridge.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.IBinder.DeathRecipient
import android.os.RemoteCallbackList
import android.os.RemoteException
import android.util.Log
import com.didichuxing.doraemonkit.aidl.IReceiverAidlInterface
import com.didichuxing.doraemonkit.aidl.ISenderAidlInterface
import java.util.*

class ABridgeService : Service() {

    private val mClients: ArrayList<Client> = ArrayList()
    private val mCallbacks = RemoteCallbackList<IReceiverAidlInterface>()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.e(TAG, "onBind")
        return mBinder
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        super.onDestroy()
        mCallbacks.kill()
    }

    private val mBinder: ISenderAidlInterface.Stub = object : ISenderAidlInterface.Stub() {
        @Throws(RemoteException::class)
        override fun join(token: IBinder) {
            val idx = findClient(token)
            if (idx >= 0) {
                Log.d(TAG, token.toString() + " already joined , client size " + mClients.size)
                return
            }
            val client = Client(token)
            // 注册客户端死掉的通知
            token.linkToDeath(client, 0)
            mClients.add(client)
            Log.d(TAG, token.toString() + " join , client size " + mClients.size)
        }

        @Throws(RemoteException::class)
        override fun leave(token: IBinder) {
            val idx = findClient(token)
            if (idx < 0) {
                Log.d(TAG, token.toString() + " already left , client size " + mClients.size)
                return
            }
            val client = mClients[idx]
            mClients.remove(client)
            // 取消注册
            client.mToken.unlinkToDeath(client, 0)
            Log.d(TAG, token.toString() + " left , client size " + mClients.size)
            if (mClients.size == 0) {
                stopSelf() //没有客户端就停止自己
            }
        }

        @Throws(RemoteException::class)
        override fun sendMessage(message: String) {
            Log.d(TAG, " sendMessage :$message")
            onSuccessCallBack(message)
        }

        @Throws(RemoteException::class)
        override fun registerCallback(cb: IReceiverAidlInterface) {
            Log.d(TAG, "registerCallback $cb")
            mCallbacks.register(cb)
        }

        @Throws(RemoteException::class)
        override fun unregisterCallback(cb: IReceiverAidlInterface) {
            Log.d(TAG, "unregisterCallback $cb")
            mCallbacks.unregister(cb)
        }
    }

    private fun findClient(token: IBinder): Int = mClients.indexOfFirst { it.mToken == token }

    private fun onSuccessCallBack(message: String) {
        val len = mCallbacks.beginBroadcast()
        for (i in 0 until len) {
            try {
                // 通知回调
                mCallbacks.getBroadcastItem(i).receiveMessage(message)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
        mCallbacks.finishBroadcast()
    }

    private inner class Client(val mToken: IBinder) : DeathRecipient {
        override fun binderDied() {
            // 客户端死掉，执行此回调
            val index = mClients.indexOf(this)
            if (index < 0) {
                return
            }
            Log.d(TAG, "client died")
            mClients.remove(this)
        }

    }

    companion object {
        private const val TAG = "ICallService"
    }

    init {
        Log.e(TAG, "launched")
    }
}