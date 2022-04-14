package com.didichuxing.doraemonkit.kit.core

import com.didichuxing.doraemonkit.BuildConfig
import com.didichuxing.doraemonkit.DoKitCallBack
import com.didichuxing.doraemonkit.config.GlobalConfig
import com.didichuxing.doraemonkit.constant.DoKitModule
import com.didichuxing.doraemonkit.kit.network.bean.WhiteHostBean
import com.didichuxing.doraemonkit.kit.network.room_db.DokitDbManager
import com.didichuxing.doraemonkit.kit.toolpanel.KitWrapItem
import com.didichuxing.doraemonkit.util.LogHelper
import com.didichuxing.doraemonkit.util.NetworkUtils
import com.didichuxing.doraemonkit.util.PathUtils
import java.io.File
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
object DoKitManager {
    const val TAG = "DoKitConstant"
    const val GROUP_ID_PLATFORM = "dk_category_platform"
    const val GROUP_ID_COMM = "dk_category_comms"
    const val GROUP_ID_WEEX = "dk_category_weex"
    const val GROUP_ID_PERFORMANCE = "dk_category_performance"
    const val GROUP_ID_UI = "dk_category_ui"
    const val GROUP_ID_LBS = "dk_category_lbs"


    /**
     * DoKit 模块能力
     */
    private val mDokitModuleAbilityMap: MutableMap<DoKitModule, DokitAbility.DokitModuleProcessor> by lazy {
        val doKitAbilities =
            ServiceLoader.load(DokitAbility::class.java, javaClass.classLoader).toList()
        val abilityMap = mutableMapOf<DoKitModule, DokitAbility.DokitModuleProcessor>()
        doKitAbilities.forEach {
            it.init()
            abilityMap[it.moduleName()] = it.getModuleProcessor()
        }
        abilityMap
    }


    /**
     * 获取ModuleProcessor
     */
    fun getModuleProcessor(module: DoKitModule): DokitAbility.DokitModuleProcessor? {
        if (mDokitModuleAbilityMap[module] == null) {
            return null
        }
        return mDokitModuleAbilityMap[module]
    }


    val SYSTEM_KITS_BAK_PATH: String by lazy {
        "${PathUtils.getInternalAppFilesPath()}${File.separator}system_kit_bak_${BuildConfig.DOKIT_VERSION}.json"
    }

    /**
     * 工具面板RV上次的位置
     */
    var TOOL_PANEL_RV_LAST_DY = 0


    /**
     * 全局的Kits
     */
    @JvmField
    val GLOBAL_KITS: LinkedHashMap<String, MutableList<KitWrapItem>> = LinkedHashMap()

    /**
     * 全局系统内置kit
     */
    @JvmField
    val GLOBAL_SYSTEM_KITS: LinkedHashMap<String, MutableList<KitWrapItem>> = LinkedHashMap()

    /**
     * 加密数据库账号密码配置
     */
    var DATABASE_PASS = mapOf<String, String>()

    /**
     * 平台端文件管理端口号
     */
    var FILE_MANAGER_HTTP_PORT = 8089

    /**
     * 一机多控长连接端口号
     */
    var MC_WS_PORT = 4444

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
    var ALWAYS_SHOW_MAIN_ICON = true

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
     * h5 js 注入代码开关
     */
    @JvmField
    var H5_JS_INJECT = false


    /**
     * h5 vConsole 注入代码开关
     */
    @JvmField
    var H5_VCONSOLE_INJECT = false

    /**
     * h5 dokit for web 注入代码开关
     */
    @JvmField
    var H5_DOKIT_MC_INJECT = false

    @JvmField
    var H5_MC_JS_INJECT_MODE = "file"

    @JvmField
    var H5_MC_JS_INJECT_URL = "http://120.55.183.20/dokit/mc/dokit.js"

    /**
     * 是否允许上传统计信息
     */
    var ENABLE_UPLOAD = true
    val ACTIVITY_LIFECYCLE_INFOS: MutableMap<String, ActivityLifecycleStatusInfo?> by lazy {
        mutableMapOf<String, ActivityLifecycleStatusInfo?>()
    }

    /**
     * 一机多控从机自定义处理器
     */
    var MC_CLIENT_PROCESSOR: McClientProcessor? = null

    /**
     * 全局回调
     */
    var CALLBACK: DoKitCallBack? = null

    /**
     *  一机多控地址
     */
    @JvmField
    var MC_CONNECT_URL: String = ""


    /**
     * Wifi IP 地址
     */
    val IP_ADDRESS_BY_WIFI: String
        get() {
            return try {
                NetworkUtils.getIpAddressByWifi()
            } catch (e: Exception) {
                LogHelper.e(TAG, "get wifi address error===>${e.message}")
                "0.0.0.0"
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
            return try {
                Class.forName("com.didichuxing.doraemonkit.DoraemonKitRpc")
                true
            } catch (e: ClassNotFoundException) {
                false
            }
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
