package com.didichuxing.doraemonkit.kit.toolpanel

import com.blankj.utilcode.util.GsonUtils
import com.didichuxing.doraemonkit.constant.DokitConstant
import com.didichuxing.doraemonkit.kit.AbstractKit
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

        fun json2SystemKits(json: String) {
            val localKits: MutableList<KitGroupBean> = GsonUtils.fromJson(json, GsonUtils.getListType(KitGroupBean::class.java))
            localKits.forEach { group ->
                DokitConstant.GLOBAL_SYSTEM_KITS[group.groupId] = mutableListOf()
                group.kits.forEach { kitBean ->
                    try {
                        //有可能不存在该模块
                        val kit: AbstractKit = Class.forName(kitBean.allClassName).newInstance() as AbstractKit
                        DokitConstant.GLOBAL_SYSTEM_KITS[group.groupId]?.add(kit)
                    } catch (e: Exception) {

                    }
                }
            }


            for (groupId: String in DokitConstant.GLOBAL_SYSTEM_KITS.keys) {
                DokitConstant.GLOBAL_KITS[groupId] = DokitConstant.GLOBAL_SYSTEM_KITS[groupId]!!
            }
        }


    }


}