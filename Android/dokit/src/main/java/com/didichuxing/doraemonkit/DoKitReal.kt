package com.didichuxing.doraemonkit

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.didichuxing.doraemonkit.config.GlobalConfig
import com.didichuxing.doraemonkit.config.PerformanceSpInfoConfig
import com.didichuxing.doraemonkit.constant.DoKitModule
import com.didichuxing.doraemonkit.constant.SharedPrefsKey
import com.didichuxing.doraemonkit.datapick.DataPickManager
import com.didichuxing.doraemonkit.extension.doKitGlobalExceptionHandler
import com.didichuxing.doraemonkit.extension.doKitGlobalScope
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.core.*
import com.didichuxing.doraemonkit.kit.health.AppHealthInfoUtil
import com.didichuxing.doraemonkit.kit.health.model.AppHealthInfo.DataBean.BigFileBean
import com.didichuxing.doraemonkit.kit.network.NetworkManager
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.DokitExtInterceptor
import com.didichuxing.doraemonkit.kit.timecounter.instrumentation.HandlerHooker
import com.didichuxing.doraemonkit.kit.toolpanel.KitWrapItem
import com.didichuxing.doraemonkit.kit.toolpanel.ToolPanelUtil
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorManager
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorManager.WebDoorCallback
import com.didichuxing.doraemonkit.util.*
import kotlinx.coroutines.*
import java.io.File
import java.util.*

/**
 * Created by jintai on 2019/12/18.
 * DoKit 真正执行的类  不建议外部app调用
 */
object DoKitReal {


    private lateinit var APPLICATION: Application
    var isInit = false

    fun setDebug(debug: Boolean) {
        LogHelper.setDebug(debug)
    }

    /**
     * @param app
     * @param mapKits  自定义kits  根据用户传进来的分组 建议优选选择mapKits 两者都传的话会选择mapKits
     * @param listKits  自定义kits
     * @param productId Dokit平台端申请的productId
     */
    fun install(
        app: Application,
        mapKits: LinkedHashMap<String, List<AbstractKit>>,
        listKits: List<AbstractKit>,
        productId: String
    ) {

        pluginConfig()
        initThirdLibraryInfo()
        DoKitManager.PRODUCT_ID = productId
        DoKitManager.APP_HEALTH_RUNNING = GlobalConfig.getAppHealth()
        //赋值
        APPLICATION = app
        //初始化工具类
        initAndroidUtil(app)


        //判断进程名
        if (!ProcessUtils.isMainProcess()) {
            return
        }

        //解锁系统隐藏api限制权限以及hook Instrumentation
        HandlerHooker.doHook(app)

        val strDokitMode = DoKitSPUtil.getString(SharedPrefsKey.FLOAT_START_MODE, "normal")
        DoKitManager.IS_NORMAL_FLOAT_MODE = strDokitMode == "normal"
        //初始化第三方工具
        //建议业务自己接入
        //installLeakCanary(app)
        checkLargeImgIsOpen()
        registerNetworkStatusChangedListener()
        startAppHealth()
        initGpsMock()

        globalRunTimeHook()

        //注册全局的activity生命周期回调
        app.registerActivityLifecycleCallbacks(DoKitActivityLifecycleCallbacks())
        //注册App前后台切换监听
        registerAppStatusChangedListener()
        //DokitConstant.KIT_MAPS.clear()
        DoKitManager.GLOBAL_KITS.clear()
        //添加用户的自定义kit
        when {
            mapKits.isNotEmpty() -> {
                mapKits.forEach { map ->
                    val kitWraps: MutableList<KitWrapItem> = map.value.map {
                        KitWrapItem(
                            KitWrapItem.TYPE_KIT,
                            DoKitCommUtil.getString(it.name),
                            true,
                            map.key,
                            it
                        )
                    } as MutableList<KitWrapItem>

                    DoKitManager.GLOBAL_KITS[map.key] = kitWraps
                }
            }

            mapKits.isEmpty() && listKits.isNotEmpty() -> {
                val kitWraps: MutableList<KitWrapItem> = listKits.map {
                    KitWrapItem(
                        KitWrapItem.TYPE_KIT,
                        DoKitCommUtil.getString(it.name),
                        true,
                        DoKitCommUtil.getString(R.string.dk_category_biz),
                        it
                    )
                } as MutableList<KitWrapItem>
                DoKitManager.GLOBAL_KITS[DoKitCommUtil.getString(R.string.dk_category_biz)] =
                    kitWraps
            }

        }

        //添加自定义的kit 需要读取配置文件
        doKitGlobalScope.launch(CoroutineName("DoKit全局异常") + doKitGlobalExceptionHandler) {
            addInnerKit(app)
        }


        //添加自定义的kit 需要读取配置文件
//        ThreadUtils.executeByIo(object : ThreadUtils.SimpleTask<Any>() {
//            override fun doInBackground(): Any {
//                addInnerKit(app)
//                return Any()
//            }
//
//            override fun onSuccess(result: Any?) {
//            }
//        })

        //addSystemKitForTest(app)
        //初始化悬浮窗管理类
        DoKitViewManager.INSTANCE.init()
        //上传app基本信息便于统计
        if (DoKitManager.ENABLE_UPLOAD) {
            try {
                DoraemonStatisticsUtil.uploadUserInfo(app)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        //上传埋点
        DataPickManager.getInstance().postData()
        isInit = true
    }


    /**
     * 注册App前后台切换监听
     */
    private fun registerAppStatusChangedListener() {
        //前后台监听
        AppUtils.registerAppStatusChangedListener(object : Utils.OnAppStatusChangedListener {
            //进入前台
            override fun onForeground(activity: Activity?) {
                DoKitServiceManager.dispatch(
                    DoKitServiceEnum.onForeground,
                    activity!!
                )
            }

            //进入后台
            override fun onBackground(activity: Activity?) {
                DoKitServiceManager.dispatch(
                    DoKitServiceEnum.onBackground,
                    activity!!
                )
            }

        })
    }


    /**
     * 添加内置kit
     */
    private suspend fun addInnerKit(application: Application) = withContext(Dispatchers.IO) {
        var json: String?
        if (FileUtils.isFileExists(DoKitManager.SYSTEM_KITS_BAK_PATH)) {
            json = FileIOUtils.readFile2String(DoKitManager.SYSTEM_KITS_BAK_PATH)
            if (TextUtils.isEmpty(json) || json == "[]") {
                val open = application.assets.open("dokit_system_kits.json")
                json = ConvertUtils.inputStream2String(open, "UTF-8")
            }
        } else {
            val open = application.assets.open("dokit_system_kits.json")
            json = ConvertUtils.inputStream2String(open, "UTF-8")
        }

        ToolPanelUtil.jsonConfig2InnerKits(json)
        //悬浮窗模式
        DoKitManager.GLOBAL_KITS[DoKitCommUtil.getString(R.string.dk_category_mode)] =
            mutableListOf()
        //添加退出项
        DoKitManager.GLOBAL_KITS[DoKitCommUtil.getString(R.string.dk_category_exit)] =
            mutableListOf()
        //版本号
        DoKitManager.GLOBAL_KITS[DoKitCommUtil.getString(R.string.dk_category_version)] =
            mutableListOf()

        //遍历初始化
        DoKitManager.GLOBAL_KITS.forEach { map ->
            map.value.forEach { kitWrap ->
                kitWrap.kit?.onAppInit(application)
            }
        }
    }


    /**
     * 插件会在当前方法中插入插件配置代码
     */
    private fun pluginConfig() {}

    /**
     * 插件会在当前方法中插入三方库的基本信息
     */
    private fun initThirdLibraryInfo() {
    }

    /**
     * 全局方法hook
     */
    private fun globalRunTimeHook() {
        try {
            val mcProcessor = DoKitManager.getModuleProcessor(DoKitModule.MODULE_MC)
            mcProcessor?.proceed(mapOf("action" to "global_hook"))
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 获取MC当前的链接地址
     */
    fun getMcConnectUrl(): String {
        try {
            val mcProcessor = DoKitManager.getModuleProcessor(DoKitModule.MODULE_MC)
            val map = mcProcessor?.proceed(mapOf("action" to "dokit_mc_connect_url"))
            val url = map?.get("url") as String
            return url
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
    private fun initGpsMock(){
        val map = mapOf(
            "action" to "init_gps_mock"
        )
        DoKitManager.getModuleProcessor(DoKitModule.MODULE_GPS_MOCK)?.proceed(map)
    }

    /**
     * 单个文件的阈值为1M
     */
    private const val FILE_LENGTH_THRESHOLD = 1 * 1024 * 1024.toLong()

    //todo 测试时为1k 对外时需要修改回来
    //private static long FILE_LENGTH_THRESHOLD = 1024;
    private fun traverseFile(rootFileDir: File?) {
        if (rootFileDir == null) {
            return
        }
        val files = rootFileDir.listFiles()
        files?.forEach { file ->
            if (file.isDirectory) {
                //若是目录，则递归打印该目录下的文件
                //LogHelper.i(TAG, "文件夹==>" + file.getAbsolutePath());
                traverseFile(file)
            }
            if (file.isFile) {
                //若是文件，直接打印 byte
                val fileLength = FileUtils.getLength(file)
                if (fileLength > FILE_LENGTH_THRESHOLD) {
                    val fileBean = BigFileBean()
                    fileBean.fileName = FileUtils.getFileName(file)
                    fileBean.filePath = file.absolutePath
                    fileBean.fileSize = "" + fileLength
                    AppHealthInfoUtil.getInstance().addBigFilrInfo(fileBean)
                }
                //LogHelper.i(TAG, "文件==>" + file.getAbsolutePath() + "   fileName===>" + FileUtils.getFileName(file) + " fileLength===>" + fileLength);
            }
        }

    }

    /**
     * 开启大文件检测
     * https://blog.csdn.net/csdn_aiyang/article/details/80665185 内部存储和外部存储的概念
     */
    private fun startBigFileInspect() {
        ThreadUtils.executeByIo(object : ThreadUtils.SimpleTask<Any?>() {
            @Throws(Throwable::class)
            override fun doInBackground(): Any? {
                val externalCacheDir =
                    APPLICATION.externalCacheDir
                if (externalCacheDir != null) {
                    val externalRootDir = externalCacheDir.parentFile
                    traverseFile(externalRootDir)
                }
                val innerCacheDir = APPLICATION.cacheDir
                if (innerCacheDir != null) {
                    val innerRootDir = innerCacheDir.parentFile
                    traverseFile(innerRootDir)
                }
                return null
            }

            override fun onSuccess(result: Any?) {}
        })
    }

    /**
     * 开启健康体检
     */
    private fun startAppHealth() {
        if (!DoKitManager.APP_HEALTH_RUNNING) {
            return
        }
        if (TextUtils.isEmpty(DoKitManager.PRODUCT_ID)) {
            ToastUtils.showShort("要使用健康体检功能必须先去平台端注册")
            return
        }
        AppHealthInfoUtil.getInstance().start()
        //开启大文件检测
        startBigFileInspect()
    }

    fun setWebDoorCallback(callback: WebDoorCallback?) {
        WebDoorManager.getInstance().webDoorCallback = callback
    }

    /**
     * 注册全局的网络状态监听
     */
    private fun registerNetworkStatusChangedListener() {
        NetworkUtils.registerNetworkStatusChangedListener(object :
            NetworkUtils.OnNetworkStatusChangedListener {
            override fun onDisconnected() {
                //ToastUtils.showShort("当前网络已断开");
                Log.i("Doraemon", "当前网络已断开")

            }

            override fun onConnected(networkType: NetworkUtils.NetworkType) {
                //重启DebugDB
                //ToastUtils.showShort("当前网络类型:" + networkType.name());
                Log.i("Doraemon", "当前网络类型" + networkType.name)

            }
        })
    }

    /**
     * 确认大图检测功能时候被打开
     */
    private fun checkLargeImgIsOpen() {
        if (PerformanceSpInfoConfig.isLargeImgOpen()) {
            NetworkManager.get().startMonitor()
        }
    }

    /**
     * 安装leackCanary
     *
     * @param app
     */
    private fun installLeakCanary(app: Application) {
        //反射调用
        try {
            val leakCanaryManager = Class.forName("com.didichuxing.doraemonkit.LeakCanaryManager")
            val install = leakCanaryManager.getMethod("install", Application::class.java)
            //调用静态的install方法
            install.invoke(null, app)
            val initAidlBridge =
                leakCanaryManager.getMethod("initAidlBridge", Application::class.java)
            //调用静态initAidlBridge方法
            initAidlBridge.invoke(null, app)
        } catch (e: Exception) {
        }
    }

    private fun initAndroidUtil(app: Application) {
        Utils.init(app)
        LogUtils.getConfig()
            // 设置 log 总开关，包括输出到控制台和文件，默认开
            .setLogSwitch(true)
            // 设置是否输出到控制台开关，默认开
            .setConsoleSwitch(true)
            // 设置 log 全局标签，默认为空，当全局标签不为空时，我们输出的 log 全部为该 tag， 为空时，如果传入的 tag 为空那就显示类名，否则显示 tag
            .setGlobalTag("Doraemon")
            // 设置 log 头信息开关，默认为开
            .setLogHeadSwitch(true)
            // 打印 log 时是否存到文件的开关，默认关
            .setLog2FileSwitch(true)
            // 当自定义路径为空时，写入应用的/cache/log/目录中
            .setDir("")
            // 当文件前缀为空时，默认为"util"，即写入文件为"util-MM-dd.txt"
            .setFilePrefix("djx-table-log")
            // 输出日志是否带边框开关，默认开
            .setBorderSwitch(true)
            // 一条日志仅输出一条，默认开，为美化 AS 3.1 的 Logcat
            .setSingleTagSwitch(true)
            // log 的控制台过滤器，和 logcat 过滤器同理，默认 Verbose
            .setConsoleFilter(LogUtils.V)
            // log 文件过滤器，和 logcat 过滤器同理，默认 Verbose
            .setFileFilter(LogUtils.E)
            // log 栈深度，默认为 1
            .setStackDeep(2)
            .stackOffset = 0
    }

    /**
     * 显示系统悬浮窗icon
     */
    private fun showMainIcon() {
        DoKitViewManager.INSTANCE.attachMainIcon(ActivityUtils.getTopActivity())
    }

    fun show() {
        DoKitManager.ALWAYS_SHOW_MAIN_ICON = true
        if (!isShow) {
            showMainIcon()
        }
    }

    /**
     * 直接显示工具面板页面
     */
    fun showToolPanel() {
        DoKitViewManager.INSTANCE.attachToolPanel(ActivityUtils.getTopActivity())
    }

    fun hideToolPanel() {
        DoKitViewManager.INSTANCE.detachToolPanel()
    }

    fun hide() {
        DoKitManager.MAIN_ICON_HAS_SHOW = false
        DoKitManager.ALWAYS_SHOW_MAIN_ICON = false
        DoKitViewManager.INSTANCE.detachMainIcon()
    }

    fun sendCustomEvent(eventType: String, view: View? = null, param: Map<String, String>? = null) {
        val map = mapOf(
            "action" to "mc_custom_event",
            "eventType" to eventType,
            "view" to view,
            "param" to param
        )
        DoKitManager.getModuleProcessor(DoKitModule.MODULE_MC)?.proceed(map)
    }

    fun getMode(): String {
        val map = mapOf(
            "action" to "mc_mode"
        )
        val result = DoKitManager.getModuleProcessor(DoKitModule.MODULE_MC)?.proceed(map)
        return result?.get("mode") as String
    }

    /**
     * 禁用app信息上传开关，该上传信息只为做DoKit接入量的统计，如果用户需要保护app隐私，可调用该方法进行禁用
     */
    fun disableUpload() {
        DoKitManager.ENABLE_UPLOAD = false
    }

    val isShow: Boolean
        get() = DoKitManager.MAIN_ICON_HAS_SHOW

    /**
     * 设置加密数据库的密码
     */
    fun setDatabasePass(map: Map<String, String>) {
        DoKitManager.DATABASE_PASS = map
    }

    /**
     * 设置平台端文件管理端口号
     */
    fun setFileManagerHttpPort(port: Int) {
        DoKitManager.FILE_MANAGER_HTTP_PORT = port
    }

    /**
     * 设置一机多控长连接端口号
     */
    fun setMCWSPort(port: Int) {
        DoKitManager.MC_WS_PORT = port
    }

    /**
     * 是否显示主入口icon
     */
    fun setAlwaysShowMainIcon(alwaysShow: Boolean) {
        DoKitManager.ALWAYS_SHOW_MAIN_ICON = alwaysShow
    }


    /**
     * 设置一机多控自定义拦截器
     */
    fun setMCIntercept(interceptor: McClientProcessor) {
        DoKitManager.MC_CLIENT_PROCESSOR = interceptor
    }

    /**
     * 设置扩展网络拦截器的代理对象
     */
    fun setNetExtInterceptor(extInterceptorProxy: DokitExtInterceptor.DokitExtInterceptorProxy) {
        DokitExtInterceptor.dokitExtInterceptorProxy = extInterceptorProxy
    }


    /**
     * 设置一机多控自定义拦截器
     */
    fun setCallBack(callback: DoKitCallBack) {
        DoKitManager.CALLBACK = callback
    }


    /**
     * 启动悬浮窗
     * @JvmStatic:允许使用java的静态方法的方式调用
     * @JvmOverloads :在有默认参数值的方法中使用@JvmOverloads注解，则Kotlin就会暴露多个重载方法。
     */
    fun launchFloating(
        targetClass: Class<out AbsDoKitView>,
        mode: DoKitViewLaunchMode = DoKitViewLaunchMode.SINGLE_INSTANCE,
        bundle: Bundle? = null
    ) {
        SimpleDoKitLauncher.launchFloating(targetClass, mode, bundle)
    }


    /**
     * 移除悬浮窗
     * @JvmStatic:允许使用java的静态方法的方式调用
     * @JvmOverloads :在有默认参数值的方法中使用@JvmOverloads注解，则Kotlin就会暴露多个重载方法。
     */
    fun removeFloating(targetClass: Class<out AbsDoKitView>) {
        SimpleDoKitLauncher.removeFloating(targetClass)
    }

    /**
     * 移除悬浮窗
     * @JvmStatic:允许使用java的静态方法的方式调用
     * @JvmOverloads :在有默认参数值的方法中使用@JvmOverloads注解，则Kotlin就会暴露多个重载方法。
     */
    fun removeFloating(dokitView: AbsDoKitView) {
        SimpleDoKitLauncher.removeFloating(dokitView)
    }

    /**
     * 启动全屏页面
     * @JvmStatic:允许使用java的静态方法的方式调用
     * @JvmOverloads :在有默认参数值的方法中使用@JvmOverloads注解，则Kotlin就会暴露多个重载方法。
     */
    fun launchFullScreen(
        targetClass: Class<out BaseFragment>,
        context: Context? = null,
        bundle: Bundle? = null,
        isSystemFragment: Boolean = false
    ) {
        SimpleDoKitLauncher.launchFullScreen(targetClass, context, bundle, isSystemFragment)
    }

    @JvmStatic
    fun <T : AbsDoKitView> getDoKitView(
        activity: Activity?,
        clazz: Class<out T>
    ): T? {
        return if (DoKitViewManager.INSTANCE.getDoKitView(activity, clazz) == null) {
            null
        } else {
            DoKitViewManager.INSTANCE.getDoKitView(activity, clazz) as T
        }
    }
}
