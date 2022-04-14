package com.didichuxing.doraemonkit.kit.connect.ws

import com.didichuxing.doraemonkit.kit.connect.data.TextPackage


/**
 * didi Create on 2022/4/12 .
 *
 * Copyright (c) 2022/4/12 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/12 12:05 下午
 * @Description 用一句话说明文件功能
 */

interface OnWebSocketTextPackageListener {
    fun onReceiveTextPackage(webSocket: OkHttpWebSocketSession, textPackage: TextPackage)
}
