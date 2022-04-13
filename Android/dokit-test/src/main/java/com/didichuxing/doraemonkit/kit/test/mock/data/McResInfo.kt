package com.didichuxing.doraemonkit.kit.test.mock.data

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
data class McResInfo<T>(
    val code: Int = 0,
    val msg: String = "",
    var data: T? = null
)
