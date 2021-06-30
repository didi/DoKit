package com.didichuxing.doraemonkit.kit.mc.ability

import android.app.Person

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/6/22-20:02
 * 描    述：
 * 修订历史：
 * ================================================
 */
data class McUseCaseInfo(
    val casePerson: String = "DoKit",
    val caseName: String = "DoKit Default Case",
    val appInfo: AppInfo = AppInfo(),
    val httpInfos: MutableList<HttpInfo> = mutableListOf<HttpInfo>()
)
