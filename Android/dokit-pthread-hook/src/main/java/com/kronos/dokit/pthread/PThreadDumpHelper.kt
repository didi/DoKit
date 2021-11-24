package com.kronos.dokit.pthread

import android.content.Context
import android.util.Log
import com.tencent.matrix.fd.FDDumpBridge
import com.tencent.matrix.hook.pthread.PthreadHook
import java.io.File

/**
 *
 *  @Author LiABao
 *  @Since 2021/11/16
 *
 */

// 当前没有删除这部分文件 后续需要考虑的
fun Context.dump(invoke: MutableList<PThreadEntity>.() -> Unit = {}) {
    try {
        val parent = "$cacheDir/pthread"
        val output = "$parent/pthread_hook_${System.currentTimeMillis() / 1000L}.log"
        parent.createDirectory()
        PthreadHook.INSTANCE.dump(output)
        fdLimit()
        val pthreads = getJson(output).parserPThread()
        pthreads.forEach {
            Log.i(TAG, "pthread error :$it\n")
        }
        invoke.invoke(pthreads)
    } catch (e: Exception) {

    }
}

fun String.createDirectory() {
    val file = File(this)
    if (!file.exists()) {
        file.mkdir()
    }
}

fun fdLimit() {
    Log.i(TAG, "FD limit = " + FDDumpBridge.getFDLimit())
}

fun getJson(path: String): String {
    val stream = File(path).bufferedReader().readText()
    return stream.apply {
        Log.i(TAG, "pThread:$this")
    }
}

const val TAG = "PThreadDumpHelper"


