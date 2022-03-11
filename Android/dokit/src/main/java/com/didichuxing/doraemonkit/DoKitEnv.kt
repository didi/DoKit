package com.didichuxing.doraemonkit

import android.app.Application
import android.graphics.Point

/**
 * Created by alvince on 2021/9/29
 *
 * @author alvince.zy@gmail.com
 */
object DoKitEnv {

    @Volatile
    var app: Application? = null

    val windowSize: Point= Point()

    @JvmStatic
    fun requireApp(): Application {
        return app ?: throw IllegalStateException("Dokit app no set")
    }
}
