package com.didichuxing.doraemondemo.module.http

import android.util.Log
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ThreadUtils
import com.didichuxing.doraemondemo.old.MainDebugActivityOkhttpV3
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object URLConnectionMock {

    fun get(url: String) {
        ThreadUtils.executeByIo(object : ThreadUtils.SimpleTask<String?>() {
            @Throws(Throwable::class)
            override fun doInBackground(): String {
                try {
                    val url = URL(url.trim())
                    //打开连接
                    val urlConnection = url.openConnection() as HttpURLConnection
                    //urlConnection.setRequestProperty("token", "10051:abc");
                    //urlConnection.setRequestProperty("Content-type", "application/json");
                    //int log = urlConnection.getResponseCode();
                    //得到输入流
                    val `is` = urlConnection.inputStream
                    return ConvertUtils.inputStream2String(`is`, "utf-8")
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                return "error"
            }


            override fun onSuccess(result: String?) {
                Log.i(MainDebugActivityOkhttpV3.TAG, "httpUrlConnection====response===>===>$result")
            }
        })
    }
}
