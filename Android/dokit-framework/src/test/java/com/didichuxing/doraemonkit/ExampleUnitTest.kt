package com.didichuxing.doraemonkit

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    @Throws(Exception::class)
    fun addition_isCorrect() {

        val list = listOf<Int>(1, 2, 3, 4, 5, 6, 7)
        list.forEach {
            if (it == 3) {
                return@forEach
            }
            println(it)
        }
    }

}
