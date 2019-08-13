package com.didichuxing.doraemonkit.kit.weaknetwork;

import android.os.SystemClock;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Sink;

/**
 * @author denghaha
 * created 2019-05-09 18:35
 */
public class SpeedLimitRequestBody extends RequestBody {
    private long mSpeedByte;//b/s
    private RequestBody mRequestBody;
    private BufferedSink mBufferedSink;

    public SpeedLimitRequestBody(long speed, RequestBody source) {
        this.mRequestBody = source;
        this.mSpeedByte = speed * 1024;//转成字节
    }

    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mRequestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        //该requestBody的 writeTo方法第一次被调用时，包装成ByteCountBufferedSink 控制读取速度
        if (mBufferedSink == null) {
            //mBufferedSink = Okio.buffer(sink(sink));
            //默认8K 精确到1K
            mBufferedSink = new ByteCountBufferedSink(sink(sink), 1024L);
            mRequestBody.writeTo(mBufferedSink);
        } else {
            //该requestBody的 writeTo方法非第一次调用时 正常读取
            mRequestBody.writeTo(sink);
        }
    }

    private Sink sink(final BufferedSink sink) {
        return new ForwardingSink(sink) {
            private long cacheTotalBytesWritten;
            private long cacheStartTime;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                if (cacheStartTime == 0) {
                    cacheStartTime = SystemClock.uptimeMillis();
                }

                super.write(source, byteCount);
                cacheTotalBytesWritten += byteCount;

                long endTime = SystemClock.uptimeMillis() - cacheStartTime;
                //如果在一秒内
                if (endTime <= 1000L) {
                    //大小就超出了限制
                    if (cacheTotalBytesWritten >= mSpeedByte) {
                        long sleep = 1000L - endTime;
                        SystemClock.sleep(sleep);

                        //重置计算
                        cacheStartTime = 0L;
                        cacheTotalBytesWritten = 0L;
                    }
                }
            }
        };
    }

}
