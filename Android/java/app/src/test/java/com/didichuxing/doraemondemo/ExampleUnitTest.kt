package com.didichuxing.doraemondemo

import org.junit.Test
import java.lang.reflect.Proxy

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    @Throws(Exception::class)
    fun addition_isCorrect() {
        val bus = Bus()
        println("bus===>${bus}")
        val car: ICar = Proxy.newProxyInstance(
            bus::class.java.classLoader,
            arrayOf(ICar::class.java), CarInvocationHandler(bus)
        ) as ICar


        car.drive()

//        println("car===>${car}")
        //println("drive ${car.drive()}")

    }
}