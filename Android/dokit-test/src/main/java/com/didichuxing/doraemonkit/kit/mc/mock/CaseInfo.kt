package com.didichuxing.doraemonkit.kit.mc.mock

import com.didichuxing.doraemonkit.kit.core.DoKitManager

data class CaseInfo(

    val pId: String = DoKitManager.PRODUCT_ID,
    val caseId: String = MockManager.MC_CASE_ID,
    val caseName: String,
    val personName: String
)
