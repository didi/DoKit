package com.didichuxing.doraemonkit.kit.test.mock

import com.didichuxing.doraemonkit.kit.test.mock.proxy.ProxyRequest
import com.didichuxing.doraemonkit.kit.test.mock.proxy.ProxyResponse


/**
 * didi Create on 2022/4/14 .
 *
 * Copyright (c) 2022/4/14 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/14 3:17 下午
 * @Description 用一句话说明文件功能
 */

interface OnHttpProxyMockDataListener {

    /**
     * host 是否是主机数据
     */
    fun onHttpProxyMockRequest(request: ProxyRequest, host: Boolean)

    /**
     * host 是否是主机数据
     */
    fun onHttpProxyMockResponse(response: ProxyResponse, host: Boolean)

}
