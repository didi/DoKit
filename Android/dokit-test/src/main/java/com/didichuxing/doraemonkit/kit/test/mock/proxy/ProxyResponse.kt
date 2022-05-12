package com.didichuxing.doraemonkit.kit.test.mock.proxy

import com.didichuxing.doraemonkit.kit.test.utils.DateTime


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

data class ProxyResponse(
    val did: String,
    val responseHeaders: String,
    val responseContentType: String,
    val responseBodyLength: Long,
    val responseBody: String,
    val responseCode: Int,
    val image: Boolean,
    val source: String,
    val protocol: String,
    val responseTime: String = DateTime.nowTime(),
    val requestTimeMillis: Long = DateTime.nowTimeMillis()
)
