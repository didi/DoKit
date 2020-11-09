package com.didichuxing.doraemonkit.kit.toolpanel

import com.blankj.utilcode.util.GsonUtils
import com.didichuxing.doraemonkit.constant.DoKitConstant
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.util.DokitUtil
import java.lang.Exception

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
            val localKits: MutableList<KitGroupBean> = GsonUtils.fromJson(json, GsonUtils.getListType(KitGroupBean::class.java))
            localKits.forEach { group ->
                DoKitConstant.GLOBAL_SYSTEM_KITS[group.groupId] = mutableListOf()
                group.kits.forEach { kitBean ->
                    try {
                        //有可能不存在该模块
                        val kit: AbstractKit = Class.forName(kitBean.allClassName).newInstance() as AbstractKit
                        val kitWrapItem = KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(kit.name), kitBean.checked, group.groupId, kit)
                        DoKitConstant.GLOBAL_SYSTEM_KITS[group.groupId]?.add(kitWrapItem)
                    } catch (e: Exception) {

                    }
                }
            }


            for (groupId: String in DoKitConstant.GLOBAL_SYSTEM_KITS.keys) {
                DoKitConstant.GLOBAL_KITS[groupId] = DoKitConstant.GLOBAL_SYSTEM_KITS[groupId]!!
            }
        }


    }


}