package com.didichuxing.doraemonkit.okhttp_api;


import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import okio.Buffer;
import okio.BufferedSink;
import okio.ByteString;
import okio.Okio;
import okio.Sink;
import okio.Source;
import okio.Timeout;

/**
 * 可以设置每次写入大小的BufferedSink
 * <p>
 * Created by xiandanin on 2019-05-10 16:07
 */
public  class ByteCountBufferedSinkV3 implements BufferedSink {
    private final long mByteCount;
    private final Sink mOriginalSink;
    private final BufferedSink mDelegate;

    public ByteCountBufferedSinkV3(Sink sink, long byteCount) {
        this.mOriginalSink = sink;
        this.mDelegate = Okio.buffer(mOriginalSink);
        this.mByteCount = byteCount;
    }

    @Override
    public long writeAll(Source source) throws IOException {
        if (source == null) throw new IllegalArgumentException("source == null");
        long totalBytesRead = 0;
        for (long readCount; (readCount = source.read(buffer(), mByteCount)) != -1; ) {
            totalBytesRead += readCount;
            emitCompleteSegments();
        }
        return totalBytesRead;
    }

    @Override
    public BufferedSink write(byte[] source, int offset, int byteCount) throws IOException {
        if (!isOpen()) throw new IllegalStateException("closed");
        //计算出要写入的次数
        long count = (long) Math.ceil((double) source.length / mByteCount);
        for (int i = 0; i < count; i++) {
            //让每次写入的字节数精确到mByteCount 分多次写入
            long newOffset = i * mByteCount;
            long writeByteCount = Math.min(mByteCount, source.length - newOffset);
            buffer().write(source, (int) newOffset, (int) writeByteCount);
            emitCompleteSegments();
        }
        return this;
    }

    @Override
    public BufferedSink emitCompleteSegments() throws IOException {
        final Buffer buffer = buffer();
        mOriginalSink.write(buffer, buffer.size());
        return this;
    }

    @Override
    public Buffer buffer() {
        return mDelegate.buffer();
    }

    @Override
    public BufferedSink write(ByteString byteString) throws IOException {
        return mDelegate.write(byteString);
    }

    @Override
    public BufferedSink write(byte[] source) throws IOException {
        return mDelegate.write(source);
    }

    @Override
    public BufferedSink write(Source source, long byteCount) throws IOException {
        return mDelegate.write(source, byteCount);
    }

    @Override
    public BufferedSink writeUtf8(String string) throws IOException {
        return mDelegate.writeUtf8(string);
    }

    @Override
    public BufferedSink writeUtf8(String string, int beginIndex, int endIndex) throws IOException {
        return mDelegate.writeUtf8(string, beginIndex, endIndex);
    }

    @Override
    public BufferedSink writeUtf8CodePoint(int codePoint) throws IOException {
        return mDelegate.writeUtf8CodePoint(codePoint);
    }

    @Override
    public BufferedSink writeString(String string, Charset charset) throws IOException {
        return mDelegate.writeString(string, charset);
    }

    @Override
    public BufferedSink writeString(String string, int beginIndex, int endIndex, Charset charset) throws IOException {
        return mDelegate.writeString(string, beginIndex, endIndex, charset);
    }

    @Override
    public BufferedSink writeByte(int b) throws IOException {
        return mDelegate.writeByte(b);
    }

    @Override
    public BufferedSink writeShort(int s) throws IOException {
        return mDelegate.writeShort(s);
    }

    @Override
    public BufferedSink writeShortLe(int s) throws IOException {
        return mDelegate.writeShortLe(s);
    }

    @Override
    public BufferedSink writeInt(int i) throws IOException {
        return mDelegate.writeInt(i);
    }

    @Override
    public BufferedSink writeIntLe(int i) throws IOException {
        return mDelegate.writeIntLe(i);
    }

    @Override
    public BufferedSink writeLong(long v) throws IOException {
        return mDelegate.writeLong(v);
    }

    @Override
    public BufferedSink writeLongLe(long v) throws IOException {
        return mDelegate.writeLongLe(v);
    }

    @Override
    public BufferedSink writeDecimalLong(long v) throws IOException {
        return mDelegate.writeDecimalLong(v);
    }

    @Override
    public BufferedSink writeHexadecimalUnsignedLong(long v) throws IOException {
        return mDelegate.writeHexadecimalUnsignedLong(v);
    }

    @Override
    public void flush() throws IOException {
        mDelegate.flush();
    }

    @Override
    public BufferedSink emit() throws IOException {
        return mDelegate.emit();
    }

    @Override
    public OutputStream outputStream() {
        return mDelegate.outputStream();
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        return mDelegate.write(src);
    }

    @Override
    public boolean isOpen() {
        return mDelegate.isOpen();
    }

    @Override
    public void write(Buffer source, long byteCount) throws IOException {
        mDelegate.write(source, byteCount);
    }

    @Override
    public Timeout timeout() {
        return mDelegate.timeout();
    }

    @Override
    public void close() throws IOException {
        mDelegate.close();
    }
}
