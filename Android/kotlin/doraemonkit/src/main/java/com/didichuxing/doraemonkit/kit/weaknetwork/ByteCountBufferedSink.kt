package com.didichuxing.doraemonkit.kit.weaknetwork

import okio.*
import java.io.IOException
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.charset.Charset
import kotlin.math.ceil

/**
 * 可以设置每次写入大小的BufferedSink
 * Created by zhanys on 2020-06-09.
 */
class ByteCountBufferedSink(private val mOriginalSink: Sink, private val mByteCount: Long) : BufferedSink {
    private val mDelegate: BufferedSink = Okio.buffer(mOriginalSink)

    @Throws(IOException::class)
    override fun writeAll(source: Source?): Long {
        requireNotNull(source) { "source == null" }
        var totalBytesRead: Long = 0
        var readCount: Long
        while (source.read(buffer(), mByteCount).also { readCount = it } != -1L) {
            totalBytesRead += readCount
            emitCompleteSegments()
        }
        return totalBytesRead
    }

    @Throws(IOException::class)
    override fun write(source: ByteArray, offset: Int, byteCount: Int): BufferedSink {
        check(isOpen) { "closed" }
        //计算出要写入的次数
        val count = ceil(source.size.toDouble() / mByteCount).toLong()
        for (i in 0 until count) {
            //让每次写入的字节数精确到mByteCount 分多次写入
            val newOffset = i * mByteCount
            val writeByteCount = mByteCount.coerceAtMost(source.size - newOffset)
            buffer().write(source, newOffset.toInt(), writeByteCount.toInt())
            emitCompleteSegments()
        }
        return this
    }

    @Throws(IOException::class)
    override fun emitCompleteSegments(): BufferedSink {
        val buffer = buffer()
        mOriginalSink.write(buffer, buffer.size())
        return this
    }

    override fun buffer(): Buffer {
        return mDelegate.buffer()
    }

    @Throws(IOException::class)
    override fun write(byteString: ByteString): BufferedSink {
        return mDelegate.write(byteString)
    }

    @Throws(IOException::class)
    override fun write(source: ByteArray): BufferedSink {
        return mDelegate.write(source)
    }

    @Throws(IOException::class)
    override fun write(source: Source, byteCount: Long): BufferedSink {
        return mDelegate.write(source, byteCount)
    }

    @Throws(IOException::class)
    override fun writeUtf8(string: String): BufferedSink {
        return mDelegate.writeUtf8(string)
    }

    @Throws(IOException::class)
    override fun writeUtf8(string: String, beginIndex: Int, endIndex: Int): BufferedSink {
        return mDelegate.writeUtf8(string, beginIndex, endIndex)
    }

    @Throws(IOException::class)
    override fun writeUtf8CodePoint(codePoint: Int): BufferedSink {
        return mDelegate.writeUtf8CodePoint(codePoint)
    }

    @Throws(IOException::class)
    override fun writeString(string: String, charset: Charset): BufferedSink {
        return mDelegate.writeString(string, charset)
    }

    @Throws(IOException::class)
    override fun writeString(string: String, beginIndex: Int, endIndex: Int, charset: Charset): BufferedSink {
        return mDelegate.writeString(string, beginIndex, endIndex, charset)
    }

    @Throws(IOException::class)
    override fun writeByte(b: Int): BufferedSink {
        return mDelegate.writeByte(b)
    }

    @Throws(IOException::class)
    override fun writeShort(s: Int): BufferedSink {
        return mDelegate.writeShort(s)
    }

    @Throws(IOException::class)
    override fun writeShortLe(s: Int): BufferedSink {
        return mDelegate.writeShortLe(s)
    }

    @Throws(IOException::class)
    override fun writeInt(i: Int): BufferedSink {
        return mDelegate.writeInt(i)
    }

    @Throws(IOException::class)
    override fun writeIntLe(i: Int): BufferedSink {
        return mDelegate.writeIntLe(i)
    }

    @Throws(IOException::class)
    override fun writeLong(v: Long): BufferedSink {
        return mDelegate.writeLong(v)
    }

    @Throws(IOException::class)
    override fun writeLongLe(v: Long): BufferedSink {
        return mDelegate.writeLongLe(v)
    }

    @Throws(IOException::class)
    override fun writeDecimalLong(v: Long): BufferedSink {
        return mDelegate.writeDecimalLong(v)
    }

    @Throws(IOException::class)
    override fun writeHexadecimalUnsignedLong(v: Long): BufferedSink {
        return mDelegate.writeHexadecimalUnsignedLong(v)
    }

    @Throws(IOException::class)
    override fun flush() {
        mDelegate.flush()
    }

    @Throws(IOException::class)
    override fun emit(): BufferedSink {
        return mDelegate.emit()
    }

    override fun outputStream(): OutputStream {
        return mDelegate.outputStream()
    }

    @Throws(IOException::class)
    override fun write(src: ByteBuffer): Int {
        return mDelegate.write(src)
    }

    override fun isOpen(): Boolean {
        return mDelegate.isOpen
    }

    @Throws(IOException::class)
    override fun write(source: Buffer, byteCount: Long) {
        mDelegate.write(source, byteCount)
    }

    override fun timeout(): Timeout {
        return mDelegate.timeout()
    }

    @Throws(IOException::class)
    override fun close() {
        mDelegate.close()
    }
}
