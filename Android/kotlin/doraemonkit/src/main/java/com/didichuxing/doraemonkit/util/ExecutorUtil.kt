package com.didichuxing.doraemonkit.util

import java.util.concurrent.ExecutorService
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object ExecutorUtil {
  private val sExecutorService: ExecutorService by lazy {
    ThreadPoolExecutor(
      1, 5, 60L, TimeUnit.SECONDS,
      SynchronousQueue(),
      ThreadPoolExecutor.AbortPolicy()
    )
  }

  fun execute(r: Runnable) {
    sExecutorService.execute(r)
  }

  fun execute(runnable: () -> Unit) {
    sExecutorService.execute(runnable)
  }
}