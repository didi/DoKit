package com.didichuxing.doraemonkit.kit.crash

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.widget.Toast
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.CachesKey
import com.didichuxing.doraemonkit.util.CacheUtils
import com.didichuxing.doraemonkit.util.FileUtil

import java.io.File
import java.io.Serializable
import java.util.Date

/**
 * Created by wangxueying on 2020-06-30
 */
class CrashCaptureManager private constructor() : Thread.UncaughtExceptionHandler {
    companion object {
        private val TAG = "CrashCaptureManager"
        val instance: CrashCaptureManager = CrashCaptureManager()
    }

    private val mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
    private val mHandler: Handler
    private var mContext: Context? = null

    val crashCacheDir: File
        get() {
            val dir = File(mContext!!.cacheDir.toString() + File.separator + CachesKey.CRASH_HISTORY)
            if (!dir.exists()) {
                dir.mkdir()
            }
            return dir
        }

    private val crashCacheFile: File
        get() {
            val fileName = Date().toString()
            return File(crashCacheDir.toString() + File.separator + fileName)
        }

    init {
        val handlerThread = HandlerThread(TAG)
        handlerThread.start()
        mHandler = Handler(handlerThread.looper)
    }

    fun init(context: Context?) {
        mContext = context?.applicationContext
    }

    fun start() {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    fun stop() {
        Thread.setDefaultUncaughtExceptionHandler(mDefaultHandler)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        //保存崩溃信息
        CacheUtils.saveObject(Log.getStackTraceString(e) as Serializable, crashCacheFile)
        //保存埋点数据
        //DataPickManager.getInstance().saveData2Local() TODO("功能待实现")
        post(Runnable {
            Toast.makeText(mContext, mContext!!.getString(R.string.dk_crash_capture_tips), Toast.LENGTH_SHORT).show()
        })
        postDelay(Runnable {
            mDefaultHandler?.uncaughtException(t, e)
        }, 2000)
    }

    private fun post(r: Runnable) {
        mHandler.post(r)
    }

    private fun postDelay(r: Runnable, delayMillis: Long) {
        mHandler.postDelayed(r, delayMillis)
    }

    fun clearCacheHistory() {
        FileUtil.deleteDirectory(crashCacheDir)
    }


}