package com.didichuxing.doraemonkit

import android.app.Application
import android.os.Build
import android.text.TextUtils
import android.util.Log
import com.amitshekhar.DebugDB
import com.amitshekhar.debug.encrypt.sqlite.DebugDBEncryptFactory
import com.amitshekhar.debug.sqlite.DebugDBFactory
import com.blankj.utilcode.util.*
import com.blankj.utilcode.util.NetworkUtils.OnNetworkStatusChangedListener
import com.blankj.utilcode.util.ThreadUtils.SimpleTask
import com.didichuxing.doraemonkit.aop.OkHttpHook
import com.didichuxing.doraemonkit.config.GlobalConfig
import com.didichuxing.doraemonkit.config.GpsMockConfig
import com.didichuxing.doraemonkit.config.PerformanceSpInfoConfig
import com.didichuxing.doraemonkit.constant.DokitConstant
import com.didichuxing.doraemonkit.constant.SharedPrefsKey
import com.didichuxing.doraemonkit.datapick.DataPickManager
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.alignruler.AlignRulerKit
import com.didichuxing.doraemonkit.kit.blockmonitor.BlockMonitorKit
import com.didichuxing.doraemonkit.kit.colorpick.ColorPickerKit
import com.didichuxing.doraemonkit.kit.core.DokitIntent
import com.didichuxing.doraemonkit.kit.core.DokitViewManager
import com.didichuxing.doraemonkit.kit.core.UniversalActivity
import com.didichuxing.doraemonkit.kit.crash.CrashCaptureKit
import com.didichuxing.doraemonkit.kit.dataclean.DataCleanKit
import com.didichuxing.doraemonkit.kit.dbdebug.DbDebugKit
import com.didichuxing.doraemonkit.kit.fileexplorer.FileExplorerKit
import com.didichuxing.doraemonkit.kit.gpsmock.GpsMockKit
import com.didichuxing.doraemonkit.kit.gpsmock.GpsMockManager
import com.didichuxing.doraemonkit.kit.gpsmock.ServiceHookManager
import com.didichuxing.doraemonkit.kit.health.AppHealthInfoUtil
import com.didichuxing.doraemonkit.kit.health.HealthKit
import com.didichuxing.doraemonkit.kit.health.model.AppHealthInfo.DataBean.BigFileBean
import com.didichuxing.doraemonkit.kit.largepicture.LargePictureKit
import com.didichuxing.doraemonkit.kit.layoutborder.LayoutBorderKit
import com.didichuxing.doraemonkit.kit.loginfo.LogInfoKit
import com.didichuxing.doraemonkit.kit.main.MainIconDokitView
import com.didichuxing.doraemonkit.kit.methodtrace.MethodCostKit
import com.didichuxing.doraemonkit.kit.network.MockKit
import com.didichuxing.doraemonkit.kit.network.NetworkKit
import com.didichuxing.doraemonkit.kit.network.NetworkManager
import com.didichuxing.doraemonkit.kit.parameter.cpu.CpuKit
import com.didichuxing.doraemonkit.kit.parameter.frameInfo.FrameInfoKit
import com.didichuxing.doraemonkit.kit.parameter.ram.RamKit
import com.didichuxing.doraemonkit.kit.sysinfo.DevelopmentPageKit
import com.didichuxing.doraemonkit.kit.sysinfo.LocalLangKit
import com.didichuxing.doraemonkit.kit.sysinfo.SysInfoKit
import com.didichuxing.doraemonkit.kit.timecounter.TimeCounterKit
import com.didichuxing.doraemonkit.kit.timecounter.instrumentation.HandlerHooker
import com.didichuxing.doraemonkit.kit.toolpanel.KitBean
import com.didichuxing.doraemonkit.kit.toolpanel.KitGroupBean
import com.didichuxing.doraemonkit.kit.toolpanel.KitWrapItem
import com.didichuxing.doraemonkit.kit.toolpanel.ToolPanelUtil
import com.didichuxing.doraemonkit.kit.uiperformance.UIPerformanceKit
import com.didichuxing.doraemonkit.kit.viewcheck.ViewCheckerKit
import com.didichuxing.doraemonkit.kit.weaknetwork.WeakNetworkKit
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorKit
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorManager
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorManager.WebDoorCallback
import com.didichuxing.doraemonkit.util.DokitUtil
import com.didichuxing.doraemonkit.util.DoraemonStatisticsUtil
import com.didichuxing.doraemonkit.util.LogHelper
import com.didichuxing.doraemonkit.util.SharedPrefsUtil
import java.io.File
import java.util.*

/**
 * Created by jintai on 2019/12/18.
 * DoraemonKit 真正执行的类  不建议外部app调用
 */
object DoraemonKitReal {
    private const val TAG = "Doraemon"

    /**
     * 是否允许上传统计信息
     */
    private var sEnableUpload = true
    private var APPLICATION: Application? = null

    fun setDebug(debug: Boolean) {
        LogHelper.setDebug(debug)
    }

    /**
     * @param app
     * @param mapKits  自定义kits  根据用户传进来的分组 建议优选选择mapKits 两者都传的话会选择mapKits
     * @param listKits  自定义kits
     * @param productId Dokit平台端申请的productId
     */
    fun install(app: Application, mapKits: LinkedHashMap<String, MutableList<AbstractKit>>, listKits: MutableList<AbstractKit>, productId: String) {
        pluginConfig()
        DokitConstant.PRODUCT_ID = productId
        DokitConstant.APP_HEALTH_RUNNING = GlobalConfig.getAppHealth()

        //赋值
        APPLICATION = app
        //初始化工具类
        initAndroidUtil(app)
        //判断进程名
        if (!ProcessUtils.isMainProcess()) {
            return
        }
        val strDokitMode = SharedPrefsUtil.getString(SharedPrefsKey.FLOAT_START_MODE, "normal")
        DokitConstant.IS_NORMAL_FLOAT_MODE = strDokitMode == "normal"
        //初始化第三方工具
        installLeakCanary(app)
        checkLargeImgIsOpen()
        registerNetworkStatusChangedListener()
        startAppHealth()
        checkGPSMock()

        //解锁系统隐藏api限制权限以及hook Instrumentation
        HandlerHooker.doHook(app)
        //hook WIFI GPS Telephony系统服务
        ServiceHookManager.getInstance().install(app)

        //OkHttp 拦截器 注入
        OkHttpHook.installInterceptor()

        //注册全局的activity生命周期回调
        app.registerActivityLifecycleCallbacks(DokitActivityLifecycleCallbacks())
        //DokitConstant.KIT_MAPS.clear()
        DokitConstant.GLOBAL_KITS.clear()
        //添加用户的自定义kit
        when {
            mapKits.isNotEmpty() -> {
                mapKits.forEach { map ->
                    val kitWraps: MutableList<KitWrapItem> = map.value.map {
                        KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(it.name), true, map.key, it)
                    } as MutableList<KitWrapItem>

                    DokitConstant.GLOBAL_KITS[map.key] = kitWraps
                }
            }

            mapKits.isEmpty() && listKits.isNotEmpty() -> {
                val kitWraps: MutableList<KitWrapItem> = listKits.map {
                    KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(it.name), true, DokitUtil.getString(R.string.dk_category_biz), it)
                } as MutableList<KitWrapItem>
                DokitConstant.GLOBAL_KITS[DokitUtil.getString(R.string.dk_category_biz)] = kitWraps
            }

        }
        //添加自定义的kit 需要读取配置文件
        ThreadUtils.executeByIo(object : SimpleTask<Any>() {
            override fun doInBackground(): Any {
                addInnerKit(app)
                return Any()
            }

            override fun onSuccess(result: Any?) {
            }
        })

        //addSystemKitForTest(app)
        //初始化悬浮窗管理类
        DokitViewManager.getInstance().init(app)
        //上传app基本信息便于统计
        if (sEnableUpload) {
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
     * 添加内置kit
     */
    private fun addInnerKit(application: Application) {
        val json: String?
        if (FileUtils.isFileExists(DokitConstant.SYSTEM_KITS_BAK_PATH)) {
            json = FileIOUtils.readFile2String(DokitConstant.SYSTEM_KITS_BAK_PATH)
        } else {
            val open = application.assets.open("dokit_system_kits.json")
            json = ConvertUtils.inputStream2String(open, "UTF-8")
        }

        ToolPanelUtil.jsonConfig2InnerKits(json)
        //悬浮窗模式
        DokitConstant.GLOBAL_KITS[DokitUtil.getString(R.string.dk_category_mode)] = mutableListOf()
        //添加退出项
        DokitConstant.GLOBAL_KITS[DokitUtil.getString(R.string.dk_category_exit)] = mutableListOf()
        //版本号
        DokitConstant.GLOBAL_KITS[DokitUtil.getString(R.string.dk_category_version)] = mutableListOf()

        //遍历初始化
        DokitConstant.GLOBAL_KITS.forEach { map ->
            map.value.forEach { kitWrap ->
                kitWrap.kit?.onAppInit(application)
            }
        }
    }


    /**
     * for test
     */
    private fun addSystemKit4Test(application: Application) {

        //平台工具
        val platformKits: MutableList<KitWrapItem> = mutableListOf()
        //新增数据mock工具 由于Dokit管理平台还没完善 所以暂时关闭入口
        val mockKit = MockKit()
        platformKits.add(KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(mockKit.name), true, "dk_category_platform", mockKit))
        val healKit = HealthKit()
        platformKits.add(KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(healKit.name), true, "dk_category_platform", healKit))
        DokitConstant.GLOBAL_KITS["dk_category_platform"] = platformKits

        //常用工具
        val commKits: MutableList<KitWrapItem> = mutableListOf()
        //添加工具kit
        val sysInfoKit = SysInfoKit()
        commKits.add(KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(sysInfoKit.name), true, "dk_category_comms", sysInfoKit))
        val developmentPageKit = DevelopmentPageKit()
        commKits.add(KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(developmentPageKit.name), true, "dk_category_comms", developmentPageKit))
        val localLangKit = LocalLangKit()
        commKits.add(KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(localLangKit.name), true, "dk_category_comms", localLangKit))
        val fileExplorerKit = FileExplorerKit()
        commKits.add(KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(fileExplorerKit.name), true, "dk_category_comms", fileExplorerKit))
        val gpsMockKit = GpsMockKit()
        commKits.add(KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(gpsMockKit.name), true, "dk_category_comms", gpsMockKit))
        val webDoorKit = WebDoorKit()
        commKits.add(KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(webDoorKit.name), true, "dk_category_comms", webDoorKit))
        val dataCleanKit = DataCleanKit()
        commKits.add(KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(dataCleanKit.name), true, "dk_category_comms", dataCleanKit))
        val logInfoKit = LogInfoKit()
        commKits.add(KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(logInfoKit.name), true, "dk_category_comms", logInfoKit))
        val dbDebugKit = DbDebugKit()
        commKits.add(KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(dbDebugKit.name), true, "dk_category_comms", dbDebugKit))

        DokitConstant.GLOBAL_KITS["dk_category_comms"] = commKits

        //weex专区
        val weexKits: MutableList<KitWrapItem> = mutableListOf()
        //动态添加weex专区
        try {
            val weexLogKit = Class.forName("com.didichuxing.doraemonkit.weex.log.WeexLogKit").newInstance() as AbstractKit
            commKits.add(KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(weexLogKit.name), true, "dk_category_weex", weexLogKit))
            val storageKit = Class.forName("com.didichuxing.doraemonkit.weex.storage.WeexStorageKit").newInstance() as AbstractKit
            commKits.add(KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(storageKit.name), true, "dk_category_weex", storageKit))
            val weexInfoKit = Class.forName("com.didichuxing.doraemonkit.weex.info.WeexInfoKit").newInstance() as AbstractKit
            commKits.add(KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(weexInfoKit.name), true, "dk_category_weex", weexInfoKit))
            val devToolKit = Class.forName("com.didichuxing.doraemonkit.weex.devtool.WeexDevToolKit").newInstance() as AbstractKit
            commKits.add(KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(devToolKit.name), true, "dk_category_weex", devToolKit))
            DokitConstant.GLOBAL_KITS["dk_category_weex"] = weexKits
        } catch (e: Exception) {
        }


        //性能监控
        val performanceKits: MutableList<KitWrapItem> = mutableListOf()
        //添加性能监控kit
        val frameInfoKit = FrameInfoKit()
        performanceKits.add(KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(frameInfoKit.name), true, "dk_category_performance", frameInfoKit))
        val cpuKit = CpuKit()
        performanceKits.add(KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(cpuKit.name), true, "dk_category_performance", cpuKit))
        val ramKit = RamKit()
        performanceKits.add(KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(ramKit.name), true, "dk_category_performance", ramKit))
        val networkKit = NetworkKit()
        performanceKits.add(KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(networkKit.name), true, "dk_category_performance", networkKit))
        val crashCaptureKit = CrashCaptureKit()
        performanceKits.add(KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(crashCaptureKit.name), true, "dk_category_performance", crashCaptureKit))
        val blockMonitorKit = BlockMonitorKit()
        performanceKits.add(KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(blockMonitorKit.name), true, "dk_category_performance", blockMonitorKit))
        val largePictureKit = LargePictureKit()
        performanceKits.add(KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(largePictureKit.name), true, "dk_category_performance", largePictureKit))
        val weakNetworkKit = WeakNetworkKit()
        performanceKits.add(KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(weakNetworkKit.name), true, "dk_category_performance", weakNetworkKit))
        val timeCounterKit = TimeCounterKit()
        performanceKits.add(KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(timeCounterKit.name), true, "dk_category_performance", timeCounterKit))
        val uiPerformanceKit = UIPerformanceKit()
        performanceKits.add(KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(uiPerformanceKit.name), true, "dk_category_performance", uiPerformanceKit))
        val methodCostKit = MethodCostKit()
        performanceKits.add(KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(methodCostKit.name), true, "dk_category_performance", methodCostKit))

        try {
            //动态添加leakcanary
            val leakCanaryKit = Class.forName("com.didichuxing.doraemonkit.kit.leakcanary.LeakCanaryKit").newInstance() as AbstractKit
            performanceKits.add(KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(leakCanaryKit.name), true, "dk_category_performance", leakCanaryKit))
        } catch (e: Exception) {
        }
        DokitConstant.GLOBAL_KITS["dk_category_performance"] = performanceKits


        //视觉工具
        val uiKits: MutableList<KitWrapItem> = mutableListOf()
        //添加视觉ui kit
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val colorPickerKit = ColorPickerKit()
            uiKits.add(KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(colorPickerKit.name), true, "dk_category_ui", colorPickerKit))
        }
        val alignRulerKit = AlignRulerKit()
        uiKits.add(KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(alignRulerKit.name), true, "dk_category_ui", alignRulerKit))
        val viewCheckerKit = ViewCheckerKit()
        uiKits.add(KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(viewCheckerKit.name), true, "dk_category_ui", viewCheckerKit))
        val layoutBorderKit = LayoutBorderKit()
        uiKits.add(KitWrapItem(KitWrapItem.TYPE_KIT, DokitUtil.getString(layoutBorderKit.name), true, "dk_category_ui", layoutBorderKit))
        DokitConstant.GLOBAL_KITS["dk_category_ui"] = uiKits
        //测试专用
        convert2json()

    }

    /**
     * for test
     */
    private fun convert2json() {
        val localKits = mutableListOf<KitGroupBean>()
        //遍历初始化
        DokitConstant.GLOBAL_KITS.forEach { map ->
            val kitGroupBean = KitGroupBean(map.key, mutableListOf())
            localKits.add(kitGroupBean)
            map.value.forEach { kitWrap ->
                kitGroupBean.kits.add(KitBean(kitWrap.kit!!::class.java.canonicalName!!, true, kitWrap.kit.innerKitId()))
            }
        }

        val jsonKits = GsonUtils.toJson(localKits)
        LogHelper.i(TAG, jsonKits)
    }


    /**
     * 插件会在当前方法中插入代码
     */
    private fun pluginConfig() {}
    private fun checkGPSMock() {
        if (GpsMockConfig.isGPSMockOpen()) {
            GpsMockManager.getInstance().startMock()
        }
        val latLng = GpsMockConfig.getMockLocation() ?: return
        GpsMockManager.getInstance().mockLocation(latLng.latitude, latLng.longitude)
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
        ThreadUtils.executeByIo(object : SimpleTask<Any?>() {
            @Throws(Throwable::class)
            override fun doInBackground(): Any? {
                val externalCacheDir = APPLICATION!!.externalCacheDir
                if (externalCacheDir != null) {
                    val externalRootDir = externalCacheDir.parentFile
                    traverseFile(externalRootDir)
                }
                val innerCacheDir = APPLICATION!!.cacheDir
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
        if (!DokitConstant.APP_HEALTH_RUNNING) {
            return
        }
        if (TextUtils.isEmpty(DokitConstant.PRODUCT_ID)) {
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
        NetworkUtils.registerNetworkStatusChangedListener(object : OnNetworkStatusChangedListener {
            override fun onDisconnected() {
                //ToastUtils.showShort("当前网络已断开");
                Log.i("Doraemon", "当前网络已断开")
                try {
                    DebugDB.shutDown()
                    if (DokitConstant.DB_DEBUG_FRAGMENT != null) {
                        DokitConstant.DB_DEBUG_FRAGMENT?.get()?.networkChanged(NetworkUtils.NetworkType.NETWORK_NO)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onConnected(networkType: NetworkUtils.NetworkType) {
                //重启DebugDB
                //ToastUtils.showShort("当前网络类型:" + networkType.name());
                Log.i("Doraemon", "当前网络类型" + networkType.name)
                try {
                    DebugDB.shutDown()
                    DebugDB.initialize(APPLICATION, DebugDBFactory())
                    DebugDB.initialize(APPLICATION, DebugDBEncryptFactory())
                    if (DokitConstant.DB_DEBUG_FRAGMENT != null) {
                        DokitConstant.DB_DEBUG_FRAGMENT?.get()?.networkChanged(networkType)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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
            val initAidlBridge = leakCanaryManager.getMethod("initAidlBridge", Application::class.java)
            //调用静态initAidlBridge方法
            initAidlBridge.invoke(null, app)
        } catch (e: Exception) {
        }
    }

    private fun initAndroidUtil(app: Application) {
        Utils.init(app)
        LogUtils.getConfig() // 设置 log 总开关，包括输出到控制台和文件，默认开
                .setLogSwitch(true) // 设置是否输出到控制台开关，默认开
                .setConsoleSwitch(true) // 设置 log 全局标签，默认为空，当全局标签不为空时，我们输出的 log 全部为该 tag， 为空时，如果传入的 tag 为空那就显示类名，否则显示 tag
                .setGlobalTag("Doraemon") // 设置 log 头信息开关，默认为开
                .setLogHeadSwitch(true) // 打印 log 时是否存到文件的开关，默认关
                .setLog2FileSwitch(true) // 当自定义路径为空时，写入应用的/cache/log/目录中
                .setDir("") // 当文件前缀为空时，默认为"util"，即写入文件为"util-MM-dd.txt"
                .setFilePrefix("djx-table-log") // 输出日志是否带边框开关，默认开
                .setBorderSwitch(true) // 一条日志仅输出一条，默认开，为美化 AS 3.1 的 Logcat
                .setSingleTagSwitch(true) // log 的控制台过滤器，和 logcat 过滤器同理，默认 Verbose
                .setConsoleFilter(LogUtils.V) // log 文件过滤器，和 logcat 过滤器同理，默认 Verbose
                .setFileFilter(LogUtils.E) // log 栈深度，默认为 1
                .setStackDeep(2).stackOffset = 0
    }

    /**
     * 显示系统悬浮窗icon
     */
    private fun showMainIcon() {
        if (ActivityUtils.getTopActivity() is UniversalActivity) {
            return
        }
        if (!DokitConstant.AWAYS_SHOW_MAIN_ICON) {
            return
        }
        DokitViewManager.getInstance().attachMainIcon()
        DokitConstant.MAIN_ICON_HAS_SHOW = true
    }

    fun show() {
        DokitConstant.AWAYS_SHOW_MAIN_ICON = true
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
        DokitConstant.MAIN_ICON_HAS_SHOW = false
        DokitConstant.AWAYS_SHOW_MAIN_ICON = false
        DokitViewManager.getInstance().detachMainIcon()
    }

    /**
     * 禁用app信息上传开关，该上传信息只为做DoKit接入量的统计，如果用户需要保护app隐私，可调用该方法进行禁用
     */
    fun disableUpload() {
        sEnableUpload = false
    }

    val isShow: Boolean
        get() = DokitConstant.MAIN_ICON_HAS_SHOW

    fun setDatabasePass(map: Map<String, String>) {
        DokitConstant.DATABASE_PASS = map
    }
}