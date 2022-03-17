package com.didichuxing.doraemondemo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okio.ByteString.Companion.encodeUtf8
import org.junit.Test
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.Proxy
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class KotlinBaseUnitTest {

    @Test
    @Throws(Exception::class)
    fun run() {
        a {

        }

        b {
            launch {

            }
        }

        c {

        }

        d {
            launch {

            }
        }

    }

    private fun a(block: suspend () -> Unit) {

    }

    private fun b(block: suspend CoroutineScope.() -> Unit) {

    }

    private fun c(block: () -> Unit) {

    }

    private fun d(block: CoroutineScope.() -> Unit) {

    }


}

