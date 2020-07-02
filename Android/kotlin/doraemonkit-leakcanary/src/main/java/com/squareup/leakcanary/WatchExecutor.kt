package com.squareup.leakcanary

/**
 * A [WatchExecutor] is in charge of executing a [Retryable] in the future, and retry
 * later if needed.
 */
interface WatchExecutor {
    fun execute(retryable: Retryable)

    companion object {
        @JvmField
        val NONE: WatchExecutor = object : WatchExecutor {
            override fun execute(retryable: Retryable) { }
        }
    }
}