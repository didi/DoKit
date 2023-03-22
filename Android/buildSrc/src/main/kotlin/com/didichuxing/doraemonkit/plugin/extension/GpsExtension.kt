package com.didichuxing.doraemonkit.plugin.extension


/**
 * didi Create on 2023/3/21 .
 *
 * Copyright (c) 2023/3/21 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2023/3/21 5:36 下午
 * @Description 用一句话说明文件功能
 * @see DoKitExtension
 */

class GpsExtension(
    var local: Boolean = true,
    var baidu: Boolean = true,
    var tencent: Boolean = true,
    var amap: Boolean = true,
) {


    override fun toString(): String {
        return "GpsExtension(local=$local, baidu=$baidu, tencent=$tencent, amap=$amap)"
    }
}
