package com.didichuxing.doraemondemo

import org.junit.Test

class KotlinCaseTest {


    @Test
    @Throws(Exception::class)
    fun testLet() {

        println("----------------------")
        var target :String? = null

        target?.let { name->
            println("test:: $name")
        }?:run {
            println("test:: $this")
        }
        println("*********************")
    }
}
