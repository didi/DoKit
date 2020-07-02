package com.squareup.leakcanary

/** A unit of work that can be retried later.  */
interface Retryable {
    enum class Result {
        DONE, RETRY
    }

    fun run(): Result
}