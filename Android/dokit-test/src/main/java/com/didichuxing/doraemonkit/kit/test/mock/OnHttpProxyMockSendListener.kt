package com.didichuxing.doraemonkit.kit.test.mock

import com.didichuxing.doraemonkit.kit.connect.data.TextPackage


/**
 * didi Create on 2022/4/14 .
 *
 * Copyright (c) 2022/4/14 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/14 3:16 下午
 * @Description 用一句话说明文件功能
 */

interface OnHttpProxyMockSendListener {

    fun onHttpProxyMockSend(textPackage: TextPackage)
}
