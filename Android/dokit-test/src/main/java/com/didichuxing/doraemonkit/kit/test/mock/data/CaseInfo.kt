package com.didichuxing.doraemonkit.kit.test.mock.data

import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.kit.test.mock.MockManager

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

data class CaseInfo(

    val pId: String = DoKitManager.PRODUCT_ID,
    val caseId: String = MockManager.MC_CASE_ID,
    val caseName: String,
    val personName: String
)
