package com.didichuxing.doraemonkit.kit.weaknetwork

import android.os.SystemClock
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.Buffer
import okio.BufferedSink
import okio.ForwardingSink
import okio.Sink
import java.io.IOException


/**
 * Created by zhanys on 2020-06-09.
 */
class SpeedLimitRequestBody(speed: Long, private val mRequestBody: RequestBody) : RequestBody() {
    private val mSpeedByte: Long = speed * 1024  //b/s
    private var mBufferedSink: BufferedSink? = null

    override fun contentType(): MediaType? {
        return mRequestBody.contentType()
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return mRequestBody.contentLength()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        if (mBufferedSink == null) {
            //mBufferedSink = Okio.buffer(sink(sink));
            //默认8K 精确到1K
            mBufferedSink = ByteCountBufferedSink(sink(sink), 1024L)
        }
        mBufferedSink?.apply {
            mRequestBody.writeTo(this)
            close()
        }
    }

    private fun sink(sink: BufferedSink): Sink {
        return object : ForwardingSink(sink) {
            private var cacheTotalBytesWritten: Long = 0
            private var cacheStartTime: Long = 0

            @Throws(IOException::class)
            override fun write(source: Buffer, byteCount: Long) {
                if (cacheStartTime == 0L) {
                    cacheStartTime = SystemClock.uptimeMillis()
                }
                super.write(source, byteCount)
                cacheTotalBytesWritten += byteCount
                val endTime = SystemClock.uptimeMillis() - cacheStartTime
                //如果在一秒内
                if (endTime <= 1000L) {
                    //大小就超出了限制
                    if (cacheTotalBytesWritten >= mSpeedByte) {
                        val sleep = 1000L - endTime
                        SystemClock.sleep(sleep)

                        //重置计算
                        cacheStartTime = 0L
                        cacheTotalBytesWritten = 0L
                    }
                }
            }
        }
    }
}
