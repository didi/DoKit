/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.didichuxing.doraemonkit.kit.network.okhttp.stream

import com.didichuxing.doraemonkit.kit.network.okhttp.utils.ExceptionUtil.propagate
import com.didichuxing.doraemonkit.kit.network.okhttp.utils.ExceptionUtil.propagateIfInstanceOf
import com.didichuxing.doraemonkit.kit.network.okhttp.utils.StreamUtil.copy
import java.io.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.zip.GZIPInputStream

class GunzippingOutputStream private constructor(out: OutputStream, private val mCopyFuture: Future<Void>) : FilterOutputStream(out) {
    @Throws(IOException::class)
    override fun close() {
        var success = false
        success = try {
            super.close()
            true
        } finally {
            try {
                getAndRethrow(mCopyFuture)
            } catch (e: IOException) {
                if (success) {
                    throw e
                }
            }
        }
    }

    private class GunzippingCallable(private val mIn: InputStream, private val mOut: OutputStream) : Callable<Void> {
        @Throws(IOException::class)
        override fun call(): Void? {
            val `in` = GZIPInputStream(mIn)
            try {
                copy(`in`, mOut, ByteArray(1024))
            } finally {
                `in`.close()
                mOut.close()
            }
            return null
        }

    }

    companion object {
        private val sExecutor = Executors.newCachedThreadPool()

        @Throws(IOException::class)
        fun create(finalOut: OutputStream): GunzippingOutputStream {
            val pipeIn = PipedInputStream()
            val pipeOut = PipedOutputStream(pipeIn)
            val copyFuture = sExecutor.submit(
                    GunzippingCallable(pipeIn, finalOut))
            return GunzippingOutputStream(pipeOut, copyFuture)
        }

        @Throws(IOException::class)
        private fun <T> getAndRethrow(future: Future<T>): T {
            while (true) {
                try {
                    return future.get()
                } catch (e: InterruptedException) {
                    // Continue...
                } catch (e: ExecutionException) {
                    val cause = e.cause
                    propagateIfInstanceOf(cause!!, IOException::class.java)
                    propagate(cause)
                }
            }
        }
    }

}