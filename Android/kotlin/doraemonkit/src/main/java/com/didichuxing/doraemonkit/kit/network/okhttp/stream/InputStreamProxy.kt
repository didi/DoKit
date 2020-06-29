/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.didichuxing.doraemonkit.kit.network.okhttp.stream

import com.didichuxing.doraemonkit.kit.network.okhttp.core.ResponseHandler
import java.io.ByteArrayOutputStream
import java.io.FilterInputStream
import java.io.IOException
import java.io.InputStream

class InputStreamProxy
/**
 * @param inputStream
 * @param responseHandler Special interface to intercept read events before they are sent
 * to peers via  methods.
 */(
        inputStream: InputStream?,
        private val mResponseHandler: ResponseHandler?) : FilterInputStream(inputStream) {
    private val mOutputStream = ByteArrayOutputStream()
    private var mClosed = false
    private var mSkipBuffer: ByteArray?=null

    @Synchronized
    private fun checkEOF(n: Int): Int {
        if (n == -1) {
            mResponseHandler?.onEOF(mOutputStream)
            closeOutputStreamQuietly()
        }
        return n
    }

    @Throws(IOException::class)
    override fun read(): Int {
        return try {
            val result = checkEOF(`in`.read())
            if (result != -1) {
                writeToOutputStream(result)
            }
            result
        } catch (ex: IOException) {
            throw handleIOException(ex)
        }
    }

    @Throws(IOException::class)
    override fun read(b: ByteArray): Int {
        return this.read(b, 0, b.size)
    }

    @Throws(IOException::class)
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        return try {
            val result = checkEOF(`in`.read(b, off, len))
            if (result != -1) {
                writeToOutputStream(b, off, result)
            }
            result
        } catch (ex: IOException) {
            throw handleIOException(ex)
        }
    }

    @Synchronized
    @Throws(IOException::class)
    override fun skip(n: Long): Long {
        val buffer = skipBufferLocked
        var total: Long = 0
        while (total < n) {
            val bytesDiff = n - total
            val bytesToRead = Math.min(buffer.size.toLong(), bytesDiff).toInt()
            val result = this.read(buffer, 0, bytesToRead)
            if (result == -1) {
                break
            }
            total += result.toLong()
        }
        return total
    }

    private val skipBufferLocked: ByteArray
        private get() {
            if (mSkipBuffer == null) {
                mSkipBuffer = ByteArray(BUFFER_SIZE)
            }
            return mSkipBuffer!!
        }

    override fun markSupported(): Boolean {
        // this can be implemented, but isn't needed for TeedInputStream's behavior
        return false
    }

    override fun mark(readlimit: Int) {
        // noop -- mark is not supported
    }

    @Throws(IOException::class)
    override fun reset() {
        throw UnsupportedOperationException("Mark not supported")
    }

    @Throws(IOException::class)
    override fun close() {
        super.close()
        closeOutputStreamQuietly()
    }

    @Synchronized
    private fun closeOutputStreamQuietly() {
        if (!mClosed) {
            try {
                mOutputStream.close()
            } catch (e: IOException) {
            } finally {
                mClosed = true
            }
        }
    }

    private fun handleIOException(ex: IOException): IOException {
        mResponseHandler?.onError(ex)
        return ex
    }

    @Synchronized
    private fun writeToOutputStream(oneByte: Int) {
        if (mClosed) {
            return
        }
        mOutputStream.write(oneByte)
    }

    @Synchronized
    private fun writeToOutputStream(b: ByteArray, offset: Int, count: Int) {
        if (mClosed) {
            return
        }
        mOutputStream.write(b, offset, count)
    }

    companion object {
        const val TAG = "ResponseHandlingInputStream"
        private const val BUFFER_SIZE = 1024
    }

}