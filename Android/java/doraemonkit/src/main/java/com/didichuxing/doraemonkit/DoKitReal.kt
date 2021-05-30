package com.didichuxing.doraemonkit

import android.app.Activity
import android.app.Application
import android.text.TextUtils
import android.util.Log
import com.didichuxing.doraemonkit.config.GlobalConfig
import com.didichuxing.doraemonkit.config.GpsMockConfig
import com.didichuxing.doraemonkit.config.PerformanceSpInfoConfig
import com.didichuxing.doraemonkit.constant.DoKitConstant
import com.didichuxing.doraemonkit.constant.SharedPrefsKey
import com.didichuxing.doraemonkit.datapick.DataPickManager
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.core.*
import com.didichuxing.doraemonkit.kit.gpsmock.GpsMockManager
import com.didichuxing.doraemonkit.kit.gpsmock.ServiceHookManager
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

/**
 * Created by jintai on 2019/12/18.
 * DoKit 真正执行的类  不建议外部app调用
 */
object DoKitReal {
    private const val TAG = "Doraemon"

    private lateinit var APPLICATION: Application

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

        registerModuleListener()
        pluginConfig()
        initThirdLibraryInfo()
        DoKitConstant.PRODUCT_ID = productId
        DoKitConstant.APP_HEALTH_RUNNING = GlobalConfig.getAppHealth()

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
        DoKitConstant.IS_NORMAL_FLOAT_MODE = strDokitMode == "normal"
        //初始化第三方工具
        installLeakCanary(app)
        checkLargeImgIsOpen()
        registerNetworkStatusChangedListener()
        startAppHealth()
        checkGPSMock()
        //hook WIFI GPS Telephony系统服务
        ServiceHookManager.getInstance().install(app)
        //全局运行时hook
        globalRunTimeHook()

        //注册全局的activity生命周期回调
        app.registerActivityLifecycleCallbacks(DokitActivityLifecycleCallbacks())
        //DokitConstant.KIT_MAPS.clear()
        DoKitConstant.GLOBAL_KITS.clear()
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

                    DoKitConstant.GLOBAL_KITS[map.key] = kitWraps
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
                DoKitConstant.GLOBAL_KITS[DoKitCommUtil.getString(R.string.dk_category_biz)] =
                    kitWraps
            }

        }

        //添加自定义的kit 需要读取配置文件
        val job = MainScope().launch {
            addInnerKit(app)
        }

        job.invokeOnCompletion {
            job.cancel()
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
        DokitViewManager.getInstance().init(app)
        //上传app基本信息便于统计
        if (DoKitConstant.ENABLE_UPLOAD) {
            try {
                DoraemonStatisticsUtil.uploadUserInfo(app)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        //上传埋点
        DataPickManager.getInstance().postData()
    }


    /**
     * 注册模块之间通信
     */
    private fun registerModuleListener() {
        //前后台监听
        AppUtils.registerAppStatusChangedListener(object : Utils.OnAppStatusChangedListener {
            //进入前台
            override fun onForeground(activity: Activity?) {
                DokitServiceManager.dispatch(
                    DokitServiceEnum.onForeground,
                    activity!!
                )
            }

            //进入后台
            override fun onBackground(activity: Activity?) {
                DokitServiceManager.dispatch(
                    DokitServiceEnum.onBackground,
                    activity!!
                )
            }

        })
        //跨模块通信监听
        try {
            val dokitServices =
                ServiceLoader.load(DokitServiceInterface::class.java, javaClass.classLoader)
                    .toList()
            DokitServiceManager.register(dokitServices)
        } catch (e: Exception) {

        }
    }


    /**
     * 添加内置kit
     */
    private suspend fun addInnerKit(application: Application) = withContext(Dispatchers.IO) {
        var json: String?
        if (FileUtils.isFileExists(DoKitConstant.SYSTEM_KITS_BAK_PATH)) {
            json = FileIOUtils.readFile2String(DoKitConstant.SYSTEM_KITS_BAK_PATH)
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
        DoKitConstant.GLOBAL_KITS[DoKitCommUtil.getString(R.string.dk_category_mode)] =
            mutableListOf()
        //添加退出项
        DoKitConstant.GLOBAL_KITS[DoKitCommUtil.getString(R.string.dk_category_exit)] =
            mutableListOf()
        //版本号
        DoKitConstant.GLOBAL_KITS[DoKitCommUtil.getString(R.string.dk_category_version)] =
            mutableListOf()

        //遍历初始化
        DoKitConstant.GLOBAL_KITS.forEach { map ->
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

    }

    private fun checkGPSMock() {
        if (GpsMockConfig.isGPSMockOpen()) {
            GpsMockManager.getInstance().startMock()
        }
        val latLng = GpsMockConfig.getMockLocation() ?: return
        GpsMockManager.getInstance().mockLocationWithNotify(latLng.latitude, latLng.longitude)
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
        if (!DoKitConstant.APP_HEALTH_RUNNING) {
            return
        }
        if (TextUtils.isEmpty(DoKitConstant.PRODUCT_ID)) {
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
        if (ActivityUtils.getTopActivity() is UniversalActivity) {
            return
        }
        if (!DoKitConstant.AWAYS_SHOW_MAIN_ICON) {
            return
        }
        DokitViewManager.getInstance().attachMainIcon()
        DoKitConstant.MAIN_ICON_HAS_SHOW = true
    }

    fun show() {
        DoKitConstant.AWAYS_SHOW_MAIN_ICON = true
        if (!isShow) {
            showMainIcon()
        }
    }

    /**
     * 直接显示工具面板页面
     */
    fun showToolPanel() {
        DokitViewManager.getInstance().attachToolPanel()
    }

    fun hideToolPanel() {
        DokitViewManager.getInstance().detachToolPanel()
    }

    fun hide() {
        DoKitConstant.MAIN_ICON_HAS_SHOW = false
        DoKitConstant.AWAYS_SHOW_MAIN_ICON = false
        DokitViewManager.getInstance().detachMainIcon()
    }

    /**
     * 禁用app信息上传开关，该上传信息只为做DoKit接入量的统计，如果用户需要保护app隐私，可调用该方法进行禁用
     */
    fun disableUpload() {
        DoKitConstant.ENABLE_UPLOAD = false
    }

    val isShow: Boolean
        get() = DoKitConstant.MAIN_ICON_HAS_SHOW

    /**
     * 设置加密数据库的密码
     */
    fun setDatabasePass(map: Map<String, String>) {
        DoKitConstant.DATABASE_PASS = map
    }

    /**
     * 设置平台端文件管理端口号
     */
    fun setFileManagerHttpPort(port: Int) {
        DoKitConstant.FILE_MANAGER_HTTP_PORT = port
    }

    /**
     * 设置一机多控长连接端口号
     */
    fun setMCWSPort(port: Int) {
        DoKitConstant.MC_WS_PORT = port
    }

    /**
     * 是否显示主入口icon
     */
    fun setAwaysShowMainIcon(awaysShow: Boolean) {
        DoKitConstant.AWAYS_SHOW_MAIN_ICON = awaysShow
    }


    /**
     * 设置一机多控自定义拦截器
     */
    fun setMCIntercept(interceptor: MCInterceptor) {
        DoKitConstant.MC_INTERCEPT = interceptor
    }

    /**
     * 设置扩展网络拦截器的代理对象
     */
    fun setNetExtInterceptor(extInterceptorProxy: DokitExtInterceptor.DokitExtInterceptorProxy){
        DokitExtInterceptor.dokitExtInterceptorProxy = extInterceptorProxy
    }



    /**
     * 设置一机多控自定义拦截器
     */
    fun setCallBack(callback: DoKitCallBack) {
        DoKitConstant.CALLBACK = callback
    }

}