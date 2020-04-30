package com.didichuxing.doraemonkit.constant

import android.util.SparseArray
import com.didichuxing.doraemonkit.config.GlobalConfig
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.core.group.AbsDokitGroup
import com.didichuxing.doraemonkit.kit.dbdebug.DbDebugFragment
import com.didichuxing.doraemonkit.kit.main.KitItem
import com.didichuxing.doraemonkit.kit.network.bean.WhiteHostBean
import com.didichuxing.doraemonkit.kit.network.room_db.DokitDbManager
import com.didichuxing.doraemonkit.model.ActivityLifecycleInfo
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.LinkedHashMap

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-19-10:21
 * 描    述：
 * 修订历史：
 * ================================================
 */
object DokitConstant {
    /**
     * 全局的Kits
     */
    @JvmField
    val GLOBAL_KITS: LinkedHashMap<AbsDokitGroup, MutableList<AbstractKit>> = LinkedHashMap()

    /**
     * 是否显示新的工具面板
     */
    @JvmField
    var IS_NEW_TOOL_PANEL = true

    /**
     * 产品id
     */
    @JvmField
    var PRODUCT_ID = ""

    /**
     * 是否处于健康体检中
     */
    @JvmField
    var APP_HEALTH_RUNNING = GlobalConfig.getAppHealth()

    /**
     * 是否是普通的浮标模式
     */
    @JvmField
    var IS_NORMAL_FLOAT_MODE = true

    /**
     * 是否显示icon主入口
     */
    @JvmField
    var AWAYS_SHOW_MAIN_ICON = true

    /**
     * icon主入口是否处于显示状态
     */
    @JvmField
    var MAIN_ICON_HAS_SHOW = false

    /**
     * 流量监控白名单
     */
    @JvmField
    var WHITE_HOSTS = mutableListOf<WhiteHostBean>()

    /**
     * 全局DBDebugFragment
     */
    @JvmField
    var DB_DEBUG_FRAGMENT: WeakReference<DbDebugFragment>? = null

    /**
     * 全局的dokit Kit信息
     */
    var KIT_MAPS: LinkedHashMap<Int, MutableList<AbstractKit>> = linkedMapOf()

    @JvmField
    var ACTIVITY_LIFECYCLE_INFOS = mutableMapOf<String, ActivityLifecycleInfo>()

    fun getKitList(catgory: Int): List<AbstractKit>? {
        return if (KIT_MAPS[catgory] != null) {
            ArrayList(KIT_MAPS[catgory]!!)
        } else {
            null
        }
    }

    /**
     * 将指定类目的kits转为指定的KitItems
     *
     * @param catgory
     * @return
     */
    @JvmStatic
    fun getKitItems(catgory: Int): List<KitItem>? {
        return if (KIT_MAPS[catgory] != null) {
            val kitItems: MutableList<KitItem> = ArrayList()
            for (kit in KIT_MAPS[catgory]!!) {
                kitItems.add(KitItem(kit))
            }
            kitItems
        } else {
            null
        }
    }

    /**
     * 判断接入的是否是滴滴内部的rpc sdk
     *
     * @return
     */
    @JvmStatic
    val isRpcSDK: Boolean
        get() {
            val isRpcSdk: Boolean
            isRpcSdk = try {
                Class.forName("com.didichuxing.doraemonkit.DoraemonKitRpc")
                true
            } catch (e: ClassNotFoundException) {
                false
            }
            return isRpcSdk
        }

    /**
     * 兼容滴滴内部外网映射环境  该环境的 path上会多一级/kop_xxx/路径
     *
     * @param oldPath
     * @param fromSDK
     * @return
     */
    @JvmStatic
    fun dealDidiPlatformPath(oldPath: String, fromSDK: Int): String {
        if (fromSDK == DokitDbManager.FROM_SDK_OTHER) {
            return oldPath
        }
        var newPath = oldPath
        //包含多级路径
        if (oldPath.contains("/kop") && oldPath.split("\\/").toTypedArray().size > 1) {
            //比如/kop_stable/a/b/gateway 分解以后为 "" "kop_stable" "a" "b" "gateway"
            val childPaths = oldPath.split("\\/").toTypedArray()
            val firstPath = childPaths[1]
            if (firstPath.contains("kop")) {
                newPath = oldPath.replace("/$firstPath", "")
            }
        }
        return newPath
    }
}