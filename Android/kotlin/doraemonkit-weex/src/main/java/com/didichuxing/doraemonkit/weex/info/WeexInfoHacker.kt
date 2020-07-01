package com.didichuxing.doraemonkit.weex.info

import org.apache.weex.WXEnvironment

/**
 * Transformed by alvince on 2020/6/30
 *
 * @author haojianglong
 * @date 2019-06-25
 */
object WeexInfoHacker {

    fun getWeexInfos(): List<WeexInfo> = mutableListOf<WeexInfo>()
        .apply {
            WXEnvironment.getConfig()
                .filter { entry -> !entry.key.isNullOrEmpty() }
                .map { entry -> WeexInfo(entry.key, entry.value ?: "") }
                .forEach { info -> add(info) }
        }

}
