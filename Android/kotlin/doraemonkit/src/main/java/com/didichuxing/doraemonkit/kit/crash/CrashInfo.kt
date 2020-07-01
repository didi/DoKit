package com.didichuxing.doraemonkit.kit.crash

import java.io.Serializable

/**
 * Created by wangxueying on 2020-06-30
 */
class CrashInfo : Serializable {
    var tr: Throwable? = null
    var time: Long? = null
}