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

data class ProxyRequest(
    val did: String,
    val aid: String,
    val url: String,
    val scheme: String,
    val host: String,
    val path: String,
    val query: String,
    val fragment: String,
    val requestHeaders: String,
    val requestContentType: String,
    val requestBodyLength: Long,
    val requestBody: String,
    val searchKey: String,
    val method: String,
    val clientProtocol: String,
    val requestTime: String = DateTime.nowTime(),
    val requestTimeMillis: Long = DateTime.nowTimeMillis()
)
