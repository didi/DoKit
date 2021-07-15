package com.didichuxing.doraemonkit.kit.toolpanel

import com.didichuxing.doraemonkit.util.GsonUtils
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.util.DoKitCommUtil
import java.util.*

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/5/6-16:59
 * 描    述：
 * 修订历史：
 * ================================================
 */
class ToolPanelUtil {
    companion object {

        /**
         * json 转系统kit
         */
        fun jsonConfig2InnerKits(json: String) {
            val doKitInnerKits =
                ServiceLoader.load(AbstractKit::class.java, javaClass.classLoader).toList()

            val doKitInnerKitMaps = mutableMapOf<String, AbstractKit>()
            doKitInnerKits.forEach {
                doKitInnerKitMaps[it.innerKitId()] = it
            }

            val localConfigs: MutableList<KitGroupBean> =
                GsonUtils.fromJson(json, GsonUtils.getListType(KitGroupBean::class.java))

            localConfigs.forEach { group ->
                DoKitManager.GLOBAL_SYSTEM_KITS[group.groupId] = mutableListOf()
                group.kits.forEach { kitBean ->
                    //有可能不存在该模块
                    val kit: AbstractKit? = doKitInnerKitMaps[kitBean.innerKitId]
                    kit?.let {
                        val kitWrapItem = KitWrapItem(
                            KitWrapItem.TYPE_KIT,
                            DoKitCommUtil.getString(kit.name),
                            kitBean.checked,
                            group.groupId,
                            kit
                        )
                        DoKitManager.GLOBAL_SYSTEM_KITS[group.groupId]?.add(kitWrapItem)
                    }
                }
            }


            for (groupId: String in DoKitManager.GLOBAL_SYSTEM_KITS.keys) {
                DoKitManager.GLOBAL_KITS[groupId] = DoKitManager.GLOBAL_SYSTEM_KITS[groupId]!!
            }
        }


    }


}