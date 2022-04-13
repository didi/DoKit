package com.didichuxing.doraemonkit.kit.test.event

import com.didichuxing.doraemonkit.kit.test.util.RandomIdentityUtils

/**
 * didi Create on 2022/4/13 .
 *
 * Copyright (c) 2022/4/13 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/13 3:07 下午
 * @Description 用一句话说明文件功能
 */

object ActionEventManager {


    var currentActionId: String = ""

    fun updateActionId(id: String) {
        if (id.isNullOrEmpty()) {
            currentActionId = RandomIdentityUtils.createAid()
        } else {
            currentActionId = id
        }
    }


}
