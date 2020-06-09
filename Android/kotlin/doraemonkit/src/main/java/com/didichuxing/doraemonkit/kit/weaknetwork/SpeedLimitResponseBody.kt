package com.didichuxing.doraemonkit.kit.weaknetwork

import android.os.SystemClock
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import java.io.IOException


/**
 * Created by zhanys on 2020-06-09.
 */
class SpeedLimitResponseBody internal constructor(speed: Long, private val mResponseBody: ResponseBody) : ResponseBody() {
    /**
     * 限速字节
     */
    private val mSpeedByte: Long = speed * 1024L
    private var mBufferedSource: BufferedSource? = null

    override fun contentType(): MediaType? {
        return mResponseBody.contentType()
    }

    override fun contentLength(): Long {
        return mResponseBody.contentLength()
    }

    override fun source(): BufferedSource {
        return mBufferedSource ?: Okio.buffer(source(mResponseBody.source()))
    }

    private fun source(source: Source): Source {
        return object : ForwardingSource(source) {
            /**
             * 如果小于1s 会重置
             */
            private var cacheTotalBytesRead: Long = 0

            /**
             * 分片读取1024个字节开始时间 小于1s会重置
             */
            private var cacheStartTime: Long = 0

            @Throws(IOException::class)
            override fun read(sink: Buffer, byteCount: Long): Long {
                if (cacheStartTime == 0L) {
                    cacheStartTime = SystemClock.uptimeMillis()
                }

                //默认8K 精确到1K -1代表已经读取完毕
                val bytesRead = super.read(sink.buffer(), 1024L)
                if (bytesRead == -1L) {
                    return bytesRead
                }
                //一般为1024
                cacheTotalBytesRead += bytesRead
                /**
                 * 判断当前请求累计消耗的时间 即相当于读取1024个字节所需要的时间
                 */
                val costTime = SystemClock.uptimeMillis() - cacheStartTime

                //如果每次分片读取时间小于ls sleep 延迟时间
                if (costTime <= 1000L) {
                    if (cacheTotalBytesRead >= mSpeedByte) {
                        val sleep = 1000L - costTime
                        SystemClock.sleep(sleep)
                        //重置计算
                        cacheStartTime = 0L
                        cacheTotalBytesRead = 0L
                    }
                }
                return bytesRead
            }
        }
    }

    companion object {
        private const val TAG = "SpeedLimitResponseBody"
    }

}
