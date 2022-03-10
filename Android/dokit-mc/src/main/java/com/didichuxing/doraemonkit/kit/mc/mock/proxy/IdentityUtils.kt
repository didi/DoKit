package com.didichuxing.doraemonkit.kit.mc.mock.proxy

import java.util.*

/**
 * didi Create on 2022/3/10 .
 *
 * Copyright (c) 2022/3/10 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/3/10 7:45 下午
 * @Description 用一句话说明文件功能
 */
object IdentityUtils {


    fun createPid(): String {
        val uuid = UUID.randomUUID()
        return uuid.toString()
    }

    fun createAid(): String {
        val uuid = UUID.randomUUID()
        return uuid.toString()
    }

    fun createDid(): String {
        val uuid = UUID.randomUUID()
        return uuid.toString()
    }


}
