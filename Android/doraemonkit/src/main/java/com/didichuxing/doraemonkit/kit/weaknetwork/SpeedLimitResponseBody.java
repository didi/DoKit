package com.didichuxing.doraemonkit.kit.weaknetwork;

import android.os.SystemClock;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * @author denghaha
 * created 2019-05-09 18:35
 */
public class SpeedLimitResponseBody extends ResponseBody {
    private long mSpeedByte;//b/s
    private ResponseBody mResponseBody;
    private BufferedSource mBufferedSource;

    public SpeedLimitResponseBody(long speed, ResponseBody source) {
        this.mResponseBody = source;
        this.mSpeedByte = speed * 1024L;//转成字节
    }

    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (mBufferedSource == null) {
            mBufferedSource = Okio.buffer(source(mResponseBody.source()));
        }
        return mBufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            private long cacheTotalBytesRead;
            private long cacheStartTime;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                if (cacheStartTime == 0) {
                    cacheStartTime = SystemClock.uptimeMillis();
                }

                //默认8K 精确到1K
                long bytesRead = super.read(sink.buffer(), 1024L);
                cacheTotalBytesRead += bytesRead == -1 ? 0 : bytesRead;

                long endTime = SystemClock.uptimeMillis() - cacheStartTime;

                //如果在一秒内
                if (endTime <= 1000L) {
                    //大小就超出了限制
                    if (cacheTotalBytesRead >= mSpeedByte) {
                        long sleep = 1000L - endTime;
                        SystemClock.sleep(sleep);

                        //重置计算
                        cacheStartTime = 0L;
                        cacheTotalBytesRead = 0L;
                    }
                }

                return bytesRead;
            }
        };
    }

}
