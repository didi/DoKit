package com.didichuxing.doraemonkit.weex.storage

import java.io.Serializable

/**
 * Transformed by alvince on 2020/7/1
 *
 * @author haojianglong
 * @date 2019-06-18
 */
class StorageInfo(
    var key: String = "",
    var value: String = ""
) : Serializable {

    var timestamp: String? = null

}