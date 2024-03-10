package com.didichuxing.doraemonkit.kit.mc.utils

import com.didichuxing.doraemonkit.kit.mc.oldui.DoKitMcManager
import com.didichuxing.doraemonkit.util.SPUtils


/**
 * didi Create on 2022/1/18 .
 *
 * Copyright (c) 2022/1/18 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/1/18 6:33 下午
 * @Description 用一句话说明文件功能
 */

object McCaseUtils {


    fun saveCaseId(caseId: String) {
        DoKitMcManager.MC_CASE_ID = caseId
        SPUtils.getInstance().put(DoKitMcManager.MC_CASE_ID_KEY, caseId)
    }


    fun loadCaseId(): String = when {
        DoKitMcManager.MC_CASE_ID.isEmpty() -> {
            val caseId = SPUtils.getInstance().getString(DoKitMcManager.MC_CASE_ID_KEY, "")
            DoKitMcManager.MC_CASE_ID = caseId
            DoKitMcManager.MC_CASE_ID
        }
        else -> {
            DoKitMcManager.MC_CASE_ID
        }
    }
}
