package com.didichuxing.doraemonkit.rpc.extension

import com.didichuxing.doraemonkit.util.EncodeUtils
import didihttp.RequestBody
import okio.Buffer


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

/**
 * RequestBody 转为字符串
 */
fun RequestBody?.string(): String {
    if (this != null && this.contentLength() > 0) {
        val buffer = Buffer()
        this.writeTo(buffer)
        val stringBody = EncodeUtils.urlDecode(buffer.readUtf8())
        return stringBody
    }
    return ""
}

