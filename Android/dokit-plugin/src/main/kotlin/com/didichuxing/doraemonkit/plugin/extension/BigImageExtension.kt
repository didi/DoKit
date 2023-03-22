package com.didichuxing.doraemonkit.plugin.extension


/**
 * didi Create on 2023/3/21 .
 *
 * Copyright (c) 2023/3/21 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2023/3/21 6:07 下午
 * @Description 用一句话说明文件功能
 */

class BigImageExtension(
    var glide: Boolean = true,
    var picasso: Boolean = true,
    var fresco: Boolean = true,
    var imageLoader: Boolean = true,
    var coil: Boolean = true,
) {

    override fun toString(): String {
        return "BigImageExtension(glide=$glide, picasso=$picasso, Fresco=$fresco, imageLoader=$imageLoader, coil=$coil)"
    }
}
