package com.didichuxing.doraemondemo.module.http

import android.app.ProgressDialog
import android.content.Context
import android.os.SystemClock
import android.text.format.Formatter
import android.util.Log
import android.widget.Toast
import com.didichuxing.doraemonkit.util.ThreadUtils.runOnUiThread
import okhttp3.*
import java.io.*
import java.net.SocketTimeoutException
import java.net.UnknownHostException


/**
 * didi Create on 2022/5/27 .
 *
 * Copyright (c) 2022/5/27 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/5/27 3:40 下午
 * @Description 用一句话说明文件功能
 */

object FileUploadTest {

    private var okHttpClient: OkHttpClient = OkHttpClient().newBuilder().build()

    /**
     * 模拟上传或下载文件
     *
     * @param upload true上传 false下载
     */
    fun requestByFile(context: Context, filesDir: File, upload: Boolean) {
        val dialog = ProgressDialog.show(context, null, null)
        dialog.setCancelable(true)
        var request: Request? = null
        if (upload) {
            try {
                //模拟一个1M的文件用来上传
                val length = 1L * 1024 * 1024
                val temp = File(filesDir, "test.tmp")
                if (!temp.exists() || temp.length() != length) {
                    val accessFile = RandomAccessFile(temp, "rwd")
                    accessFile.setLength(length)
                    temp.createNewFile()
                }
                request = Request.Builder()
                    .post(RequestBody.create(MediaType.parse(temp.name), temp))
                    .url("http://wallpaper.apc.360.cn/index.php?c=WallPaper&a=getAppsByOrder&order=create_time&start=0&count=1&from=360chrome")
                    .build()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            //下载一个2M的文件
            request = Request.Builder()
                .get()
                .url("http://cdn1.lbesec.com/products/history/20131220/privacyspace_rel_2.2.1617.apk")
                .build()
        }
        val call = okHttpClient!!.newCall(request!!)
        val startTime = SystemClock.uptimeMillis()
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                dialog.cancel()
                onHttpFailure(context, e)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: okhttp3.Response) {
                if (!response.isSuccessful) {
                    onFailure(call, IOException(response.message()))
                    return
                }
                val body = response.body()
                if (!upload) {
                    inputStream2File(body!!.byteStream(), File(filesDir, "test.apk"))
                }
                dialog.cancel()
                val requestLength = if (upload) call.request().body()!!.contentLength() else 0
                val responseLength = if (body!!.contentLength() < 0) 0 else body.contentLength()
                val endTime = SystemClock.uptimeMillis() - startTime
                val speed = (if (upload) requestLength else responseLength) / endTime * 1000
                val message = String.format(
                    "请求大小：%s，响应大小：%s，耗时：%dms，均速：%s/s",
                    Formatter.formatFileSize(context, requestLength),
                    Formatter.formatFileSize(context, responseLength),
                    endTime,
                    Formatter.formatFileSize(context, speed)
                )
                runOnUiThread {
                    Log.d("onResponse", message)
                    Toast.makeText(context, message, Toast.LENGTH_LONG)
                        .show()
                }
            }
        })
    }

    private fun onHttpFailure(context: Context, e: IOException) {
        e.printStackTrace()
        runOnUiThread {
            if (e is UnknownHostException) {
                Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT).show()
            } else if (e is SocketTimeoutException) {
                Toast.makeText(context, "请求超时", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun inputStream2File(`is`: InputStream, saveFile: File) {
        var len: Int
        val buf = ByteArray(2048)
        val fos = FileOutputStream(saveFile)
        `is`.use { input ->
            fos.use { output ->
                while (input.read(buf).also { len = it } != -1) {
                    output.write(buf, 0, len)
                }
                output.flush()
            }
        }
    }
}
