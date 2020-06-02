package com.didichuxing.doraemonkit.hook

import android.os.Handler
import android.os.Message
import android.text.TextUtils
import com.blankj.utilcode.util.ReflectUtils
import java.lang.reflect.Method

/**
 * @author: linjizong
 * 2019/6/3
 * @desc: 自定义的handlerCallback
 */
internal class ProxyHandlerCallback(private val mOldCallback: Handler.Callback?, val mHandler: Handler) : Handler.Callback {
    override fun handleMessage(msg: Message): Boolean {
        val msgType = preDispatch(msg)
        if (mOldCallback != null && mOldCallback.handleMessage(msg)) {
            postDispatch(msgType)
            return true
        }
        mHandler.handleMessage(msg)
        postDispatch(msgType)
        return true
    }

    private fun preDispatch(msg: Message): Int {
        when (msg.what) {
            LAUNCH_ACTIVITY -> {
                //TODO("需要kotlin实现")
            }
            PAUSE_ACTIVITY -> {
                //TODO("需要kotlin实现")
            }
            EXECUTE_TRANSACTION -> return handlerActivity(msg)
            else -> {
            }
        }
        return msg.what
    }

    private fun handlerActivity(msg: Message): Int {
        val obj = msg.obj
//        val activityCallback =   Reflector.QuietReflector.with(obj).method("getLifecycleStateRequest").call<Any>()
        val activityCallback =   ReflectUtils.reflect(obj).method("getLifecycleStateRequest").get<Method>().invoke(obj)
        if (activityCallback != null) {
            val transactionName = activityCallback.javaClass.canonicalName
            if (TextUtils.equals(transactionName, LAUNCH_ITEM_CLASS)) {
                //TimeCounterManager.get().onActivityLaunch()
                return LAUNCH_ACTIVITY
            } else if (TextUtils.equals(transactionName, PAUSE_ITEM_CLASS)) {
                //TimeCounterManager.get().onActivityPause()
                return PAUSE_ACTIVITY
            }
        }
        return msg.what
    }

    private fun postDispatch(msgType: Int) {
        when (msgType) {
            LAUNCH_ACTIVITY -> {
                //TODO("需要kotlin实现")
            }
            PAUSE_ACTIVITY -> {
                //TODO("需要kotlin实现")
            }
            else -> {
            }
        }
    }

    companion object {
        private const val TAG = "ProxyHandlerCallback"

        /**
         * Android 28开始 变量从110开始
         */
        private const val LAUNCH_ACTIVITY = 100

        /**
         * Android 28开始 变量从110开始
         */
        private const val PAUSE_ACTIVITY = 101
        private const val EXECUTE_TRANSACTION = 159
        private const val LAUNCH_ITEM_CLASS = "android.app.servertransaction.ResumeActivityItem"
        private const val PAUSE_ITEM_CLASS = "android.app.servertransaction.PauseActivityItem"
    }

}