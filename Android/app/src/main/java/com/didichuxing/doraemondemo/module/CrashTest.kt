package com.didichuxing.doraemondemo.module

object CrashTest {

    fun test() {
        checkNotNull(testCrash())
    }


    private fun testCrash(): String? {
        return null
    }
}
