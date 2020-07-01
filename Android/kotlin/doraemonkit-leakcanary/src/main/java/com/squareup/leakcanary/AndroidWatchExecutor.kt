/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.leakcanary

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import kotlin.math.pow

/**
 * [WatchExecutor] suitable for watching Android reference leaks. This executor waits for the
 * main thread to be idle then posts to a serial background thread with the delay specified by
 * [AndroidRefWatcherBuilder.watchDelay].
 */
class AndroidWatchExecutor(initialDelayMillis: Long) : WatchExecutor {
    private val mainHandler: Handler = Handler(Looper.getMainLooper())
    private val backgroundHandler: Handler
    private val initialDelayMillis: Long
    private val maxBackoffFactor: Long
    override fun execute(retryable: Retryable) {
        //主线程
        if (Looper.getMainLooper().thread === Thread.currentThread()) {
            waitForIdle(retryable, 0)
        } else {
            //异步线程
            postWaitForIdle(retryable, 0)
        }
    }

    private fun postWaitForIdle(retryable: Retryable, failedAttempts: Int) {
        mainHandler.post { waitForIdle(retryable, failedAttempts) }
    }

    private fun waitForIdle(retryable: Retryable, failedAttempts: Int) {
        // This needs to be called from the main thread.
        Looper.myQueue().addIdleHandler {
            postToBackgroundWithDelay(retryable, failedAttempts)
            false
        }
    }

    private fun postToBackgroundWithDelay(retryable: Retryable, failedAttempts: Int) {
        val exponentialBackoffFactor = failedAttempts.toDouble().pow(2.0).coerceAtMost(maxBackoffFactor.toDouble()).toLong()
        val delayMillis = initialDelayMillis * exponentialBackoffFactor
        backgroundHandler.postDelayed({
            val result = retryable.run()
            if (result === Retryable.Result.RETRY) {
                postWaitForIdle(retryable, failedAttempts + 1)
            }
        }, delayMillis)
    }

    companion object {
        const val LEAK_CANARY_THREAD_NAME = "LeakCanary-Heap-Dump"
    }

    init {
        val handlerThread = HandlerThread(LEAK_CANARY_THREAD_NAME)
        handlerThread.start()
        backgroundHandler = Handler(handlerThread.looper)
        this.initialDelayMillis = initialDelayMillis
        maxBackoffFactor = Long.MAX_VALUE / initialDelayMillis
    }
}