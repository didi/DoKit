package com.didichuxing.doraemonkit;

import android.app.Application;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.amitshekhar.DebugDB;
import com.amitshekhar.debug.encrypt.sqlite.DebugDBEncryptFactory;
import com.amitshekhar.debug.sqlite.DebugDBFactory;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ProcessUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.didichuxing.doraemonkit.aop.OkHttpHook;
import com.didichuxing.doraemonkit.config.GlobalConfig;
import com.didichuxing.doraemonkit.config.GpsMockConfig;
import com.didichuxing.doraemonkit.config.PerformanceSpInfoConfig;
import com.didichuxing.doraemonkit.constant.DokitConstant;
import com.didichuxing.doraemonkit.constant.SharedPrefsKey;
import com.didichuxing.doraemonkit.datapick.DataPickManager;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.kit.alignruler.AlignRulerKit;
import com.didichuxing.doraemonkit.kit.blockmonitor.BlockMonitorKit;
import com.didichuxing.doraemonkit.kit.colorpick.ColorPickerKit;
import com.didichuxing.doraemonkit.kit.crash.CrashCaptureKit;
import com.didichuxing.doraemonkit.kit.dataclean.DataCleanKit;
import com.didichuxing.doraemonkit.kit.dbdebug.DbDebugKit;
import com.didichuxing.doraemonkit.kit.fileexplorer.FileExplorerKit;
import com.didichuxing.doraemonkit.kit.gpsmock.GpsMockKit;
import com.didichuxing.doraemonkit.kit.gpsmock.GpsMockManager;
import com.didichuxing.doraemonkit.kit.gpsmock.ServiceHookManager;
import com.didichuxing.doraemonkit.kit.health.AppHealthInfoUtil;
import com.didichuxing.doraemonkit.kit.health.HealthKit;
import com.didichuxing.doraemonkit.kit.health.model.AppHealthInfo;
import com.didichuxing.doraemonkit.kit.largepicture.LargePictureKit;
import com.didichuxing.doraemonkit.kit.layoutborder.LayoutBorderKit;
import com.didichuxing.doraemonkit.kit.loginfo.LogInfoKit;
import com.didichuxing.doraemonkit.kit.methodtrace.MethodCostKit;
import com.didichuxing.doraemonkit.kit.mode.FloatModeKit;
import com.didichuxing.doraemonkit.kit.network.MockKit;
import com.didichuxing.doraemonkit.kit.network.NetworkKit;
import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.kit.parameter.cpu.CpuKit;
import com.didichuxing.doraemonkit.kit.parameter.frameInfo.FrameInfoKit;
import com.didichuxing.doraemonkit.kit.parameter.ram.RamKit;
import com.didichuxing.doraemonkit.kit.sysinfo.DevelopmentPageKit;
import com.didichuxing.doraemonkit.kit.sysinfo.LocalLangKit;
import com.didichuxing.doraemonkit.kit.sysinfo.SysInfoKit;
import com.didichuxing.doraemonkit.kit.temporaryclose.TemporaryCloseKit;
import com.didichuxing.doraemonkit.kit.timecounter.TimeCounterKit;
import com.didichuxing.doraemonkit.kit.timecounter.instrumentation.HandlerHooker;
import com.didichuxing.doraemonkit.kit.uiperformance.UIPerformanceKit;
import com.didichuxing.doraemonkit.kit.version.DokitVersionKit;
import com.didichuxing.doraemonkit.kit.viewcheck.ViewCheckerKit;
import com.didichuxing.doraemonkit.kit.weaknetwork.WeakNetworkKit;
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorKit;
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorManager;
import com.didichuxing.doraemonkit.model.LatLng;
import com.didichuxing.doraemonkit.ui.UniversalActivity;
import com.didichuxing.doraemonkit.ui.base.DokitIntent;
import com.didichuxing.doraemonkit.ui.base.DokitViewManager;
import com.didichuxing.doraemonkit.ui.main.MainIconDokitView;
import com.didichuxing.doraemonkit.ui.main.ToolPanelDokitView;
import com.didichuxing.doraemonkit.util.DoraemonStatisticsUtil;
import com.didichuxing.doraemonkit.util.LogHelper;
import com.didichuxing.doraemonkit.util.SharedPrefsUtil;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jintai on 2019/12/18.
 * DoraemonKit 真正执行的类  不建议外部app调用
 */
class DoraemonKitReal {
    private static final String TAG = "Doraemon";


    private static boolean sHasInit = false;

    /**
     * 是否允许上传统计信息
     */
    private static boolean sEnableUpload = true;
    private static Application APPLICATION;


    /**
     * 用来判断是否接入了dokit插件 如果安装了插件会动态修改这个值为true
     */
    private static boolean IS_HOOK = false;


    static void setDebug(boolean debug) {
        LogHelper.setDebug(debug);
    }


    static void install(Application app) {
        install(app, null);
    }

    static void install(Application app, List<AbstractKit> selfKits) {
        install(app, selfKits, "");
    }

    /**
     * @param app
     * @param selfKits  自定义kits
     * @param productId Dokit平台端申请的productId
     */
    static void install(final Application app, List<AbstractKit> selfKits, String productId) {
        DokitConstant.PRODUCT_ID = productId;
        DokitConstant.APP_HEALTH_RUNNING = GlobalConfig.getAppHealth(DoraemonKit.APPLICATION);
        //添加常用工具
        if (sHasInit) {
            //已经初始化添加自定义kits
            if (selfKits != null) {
                List<AbstractKit> biz = DokitConstant.KIT_MAPS.get(Category.BIZ);
                if (biz != null) {
                    biz.clear();
                    biz.addAll(selfKits);
                    for (AbstractKit kit : biz) {
                        kit.onAppInit(app);
                    }
                }
            }
            //aop会再次注入一遍 所以需要直接返回
            return;
        }
        sHasInit = true;
        //赋值
        APPLICATION = app;
        //初始化工具类
        initAndroidUtil(app);
        //判断进程名
        if (!ProcessUtils.isMainProcess()) {
            Log.i(TAG, "======isNotMainProcess===");
            return;
        }
        Log.i(TAG, "======isMainProcess===");


        String strDokitMode = SharedPrefsUtil.getString(app, SharedPrefsKey.FLOAT_START_MODE, "normal");
        if (strDokitMode.equals("normal")) {
            DokitConstant.IS_NORMAL_FLOAT_MODE = true;
        } else {
            DokitConstant.IS_NORMAL_FLOAT_MODE = false;
        }
        Log.i(TAG, "IS_HOOK====>" + IS_HOOK);
        //赋值全局变量
        DokitConstant.IS_HOOK = IS_HOOK;
        //初始化第三方工具
        installLeakCanary(app);

        checkLargeImgIsOpen();
        registerNetworkStatusChangedListener();
        startAppHealth();
        checkGPSMock();

        //解锁系统隐藏api限制权限以及hook Instrumentation
        HandlerHooker.doHook(app);
        //hook WIFI GPS Telephony系统服务
        ServiceHookManager.getInstance().install(app);

        //OkHttp 拦截器 注入
        OkHttpHook.installInterceptor();

        //注册全局的activity生命周期回调
        app.registerActivityLifecycleCallbacks(new DokitActivityLifecycleCallbacks());
        DokitConstant.KIT_MAPS.clear();

        //业务专区
        List<AbstractKit> biz = new ArrayList<>();
        //weex专区
        List<AbstractKit> weex = new ArrayList<>();

        //常用工具
        List<AbstractKit> tool = new ArrayList<>();
        //性能监控
        List<AbstractKit> performance = new ArrayList<>();
        //视觉工具
        List<AbstractKit> ui = new ArrayList<>();
        //平台工具
        List<AbstractKit> platform = new ArrayList<>();
        //悬浮窗模式
        List<AbstractKit> floatMode = new ArrayList<>();
        //退出
        List<AbstractKit> exit = new ArrayList<>();
        //版本号
        List<AbstractKit> version = new ArrayList<>();
        //添加工具kit
        tool.add(new SysInfoKit());
        tool.add(new DevelopmentPageKit());
        tool.add(new LocalLangKit());
        tool.add(new FileExplorerKit());
        if (GpsMockManager.getInstance().isMockEnable()) {
            tool.add(new GpsMockKit());
        }
        tool.add(new WebDoorKit());
        tool.add(new CrashCaptureKit());
        tool.add(new LogInfoKit());
        tool.add(new DataCleanKit());
        tool.add(new WeakNetworkKit());
        tool.add(new DbDebugKit());

        //添加性能监控kit
        performance.add(new FrameInfoKit());
        performance.add(new CpuKit());
        performance.add(new RamKit());
        performance.add(new NetworkKit());
        performance.add(new BlockMonitorKit());
        performance.add(new TimeCounterKit());
        performance.add(new MethodCostKit());
        performance.add(new UIPerformanceKit());
        performance.add(new LargePictureKit());

        try {
            //动态添加leakcanary
            AbstractKit leakCanaryKit = (AbstractKit) Class.forName("com.didichuxing.doraemonkit.kit.leakcanary.LeakCanaryKit").newInstance();
            performance.add(leakCanaryKit);
        } catch (Exception e) {
            //e.printStackTrace();
        }


        //添加视觉ui kit
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ui.add(new ColorPickerKit());
        }

        ui.add(new AlignRulerKit());
        ui.add(new ViewCheckerKit());
        ui.add(new LayoutBorderKit());
        //新增数据mock工具 由于Dokit管理平台还没完善 所以暂时关闭入口
        platform.add(new MockKit());
        platform.add(new HealthKit());

        //增加浮标模式
        floatMode.add(new FloatModeKit());
        //添加退出项
        exit.add(new TemporaryCloseKit());
        //添加版本号项
        version.add(new DokitVersionKit());
        //添加自定义
        if (selfKits != null && !selfKits.isEmpty()) {
            biz.addAll(selfKits);
        }
        //调用kit 初始化
        for (AbstractKit kit : biz) {
            kit.onAppInit(app);
        }
        for (AbstractKit kit : performance) {
            kit.onAppInit(app);
        }
        for (AbstractKit kit : tool) {
            kit.onAppInit(app);
        }
        for (AbstractKit kit : ui) {
            kit.onAppInit(app);
        }
        //注入到sKitMap中
        DokitConstant.KIT_MAPS.put(Category.BIZ, biz);
        //动态添加weex专区
        try {
            AbstractKit weexLogKit = (AbstractKit) Class.forName("com.didichuxing.doraemonkit.weex.log.WeexLogKit").newInstance();
            weex.add(weexLogKit);
            AbstractKit storageKit = (AbstractKit) Class.forName("com.didichuxing.doraemonkit.weex.storage.WeexStorageKit").newInstance();
            weex.add(storageKit);
            AbstractKit weexInfoKit = (AbstractKit) Class.forName("com.didichuxing.doraemonkit.weex.info.WeexInfoKit").newInstance();
            weex.add(weexInfoKit);
            AbstractKit devToolKit = (AbstractKit) Class.forName("com.didichuxing.doraemonkit.weex.devtool.WeexDevToolKit").newInstance();
            weex.add(devToolKit);
            DokitConstant.KIT_MAPS.put(Category.WEEX, weex);
        } catch (Exception e) {
            //LogHelper.e(TAG, "e====>" + e.getMessage());
        }

        DokitConstant.KIT_MAPS.put(Category.PERFORMANCE, performance);
        DokitConstant.KIT_MAPS.put(Category.PLATFORM, platform);
        DokitConstant.KIT_MAPS.put(Category.TOOLS, tool);
        DokitConstant.KIT_MAPS.put(Category.UI, ui);
        DokitConstant.KIT_MAPS.put(Category.FLOAT_MODE, floatMode);
        DokitConstant.KIT_MAPS.put(Category.CLOSE, exit);
        DokitConstant.KIT_MAPS.put(Category.VERSION, version);
        //初始化悬浮窗管理类
        DokitViewManager.getInstance().init(app);
        //上传app基本信息便于统计
        if (sEnableUpload) {
            try {
                DoraemonStatisticsUtil.uploadUserInfo(app);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //上传埋点
        DataPickManager.getInstance().postData();
    }


    private static void checkGPSMock() {
        if (GpsMockConfig.isGPSMockOpen(APPLICATION)) {
            GpsMockManager.getInstance().startMock();
        }
        LatLng latLng = GpsMockConfig.getMockLocation(APPLICATION);
        if (latLng == null) {
            return;
        }
        GpsMockManager.getInstance().mockLocation(latLng.latitude, latLng.longitude);
    }

    /**
     * 单个文件的阈值为1M
     */
    private static long FILE_LENGTH_THRESHOLD = 1 * 1024 * 1024;
    //todo 测试时为1k 对外时需要修改回来
    //private static long FILE_LENGTH_THRESHOLD = 1024;

    private static void traverseFile(File rootFileDir) {
        if (rootFileDir == null) {
            return;
        }
        File[] files = rootFileDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                //若是目录，则递归打印该目录下的文件
                //LogHelper.i(TAG, "文件夹==>" + file.getAbsolutePath());
                traverseFile(file);
            }
            if (file.isFile()) {
                //若是文件，直接打印 byte
                long fileLength = FileUtils.getLength(file);
                if (fileLength > FILE_LENGTH_THRESHOLD) {
                    AppHealthInfo.DataBean.BigFileBean fileBean = new AppHealthInfo.DataBean.BigFileBean();
                    fileBean.setFileName(FileUtils.getFileName(file));
                    fileBean.setFilePath(file.getAbsolutePath());
                    fileBean.setFileSize("" + fileLength);
                    AppHealthInfoUtil.getInstance().addBigFilrInfo(fileBean);
                }
                //LogHelper.i(TAG, "文件==>" + file.getAbsolutePath() + "   fileName===>" + FileUtils.getFileName(file) + " fileLength===>" + fileLength);
            }
        }

    }

    /**
     * 开启大文件检测
     * https://blog.csdn.net/csdn_aiyang/article/details/80665185 内部存储和外部存储的概念
     */
    private static void startBigFileInspect() {
        ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<Object>() {
            @Override
            public Object doInBackground() throws Throwable {
                File externalCacheDir = APPLICATION.getExternalCacheDir();
                if (externalCacheDir != null) {
                    File externalRootDir = externalCacheDir.getParentFile();

                    traverseFile(externalRootDir);
                }
                File innerCacheDir = APPLICATION.getCacheDir();
                if (innerCacheDir != null) {
                    File innerRootDir = innerCacheDir.getParentFile();
                    traverseFile(innerRootDir);
                }
                return null;
            }

            @Override
            public void onSuccess(Object result) {

            }
        });


    }

    /**
     * 开启健康体检
     */
    private static void startAppHealth() {
        if (!DokitConstant.APP_HEALTH_RUNNING) {
            return;
        }

        if (TextUtils.isEmpty(DokitConstant.PRODUCT_ID)) {
            ToastUtils.showShort("要使用健康体检功能必须先去平台端注册");
            return;
        }

        AppHealthInfoUtil.getInstance().start();
        //开启大文件检测
        startBigFileInspect();
    }


    static void setWebDoorCallback(WebDoorManager.WebDoorCallback callback) {
        WebDoorManager.getInstance().setWebDoorCallback(callback);
    }

    /**
     * 注册全局的网络状态监听
     */
    private static void registerNetworkStatusChangedListener() {
        NetworkUtils.registerNetworkStatusChangedListener(new NetworkUtils.OnNetworkStatusChangedListener() {
            @Override
            public void onDisconnected() {
                //ToastUtils.showShort("当前网络已断开");
                Log.i("Doraemon", "当前网络已断开");
                try {
                    DebugDB.shutDown();
                    if (DokitConstant.DB_DEBUG_FRAGMENT != null && DokitConstant.DB_DEBUG_FRAGMENT.get() != null) {
                        DokitConstant.DB_DEBUG_FRAGMENT.get().networkChanged(NetworkUtils.NetworkType.NETWORK_NO);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConnected(NetworkUtils.NetworkType networkType) {
                //重启DebugDB
                //ToastUtils.showShort("当前网络类型:" + networkType.name());
                Log.i("Doraemon", "当前网络类型" + networkType.name());
                try {
                    DebugDB.shutDown();
                    DebugDB.initialize(APPLICATION, new DebugDBFactory());
                    DebugDB.initialize(APPLICATION, new DebugDBEncryptFactory());
                    if (DokitConstant.DB_DEBUG_FRAGMENT != null && DokitConstant.DB_DEBUG_FRAGMENT.get() != null) {
                        DokitConstant.DB_DEBUG_FRAGMENT.get().networkChanged(networkType);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     * 确认大图检测功能时候被打开
     */
    private static void checkLargeImgIsOpen() {
        if (PerformanceSpInfoConfig.isLargeImgOpen()) {
            NetworkManager.get().startMonitor();
        }
    }

    /**
     * 安装leackCanary
     *
     * @param app
     */
    private static void installLeakCanary(Application app) {
        //反射调用
        try {
            Class leakCanaryManager = Class.forName("com.didichuxing.doraemonkit.LeakCanaryManager");
//            Method install = leakCanaryManager.getMethod("install", Application.class);
//            //调用静态的install方法
//            install.invoke(null, app);

            Method initAidlBridge = leakCanaryManager.getMethod("initAidlBridge", Application.class);
            //调用静态initAidlBridge方法
            initAidlBridge.invoke(null, app);
        } catch (Exception e) {
        }

    }

    private static void initAndroidUtil(Application app) {
        Utils.init(app);
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
                // 设置栈偏移，比如二次封装的话就需要设置，默认为 0
                .setStackOffset(0);
    }


    /**
     * 显示系统悬浮窗icon
     */
    private static void showSystemMainIcon() {
        if (ActivityUtils.getTopActivity() instanceof UniversalActivity) {
            return;
        }

        if (!DokitConstant.AWAYS_SHOW_MAIN_ICON) {
            return;
        }

        DokitIntent intent = new DokitIntent(MainIconDokitView.class);
        intent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
        DokitViewManager.getInstance().attach(intent);
        DokitConstant.MAIN_ICON_HAS_SHOW = true;
    }


    static void show() {
        DokitConstant.AWAYS_SHOW_MAIN_ICON = true;
        if (!isShow()) {
            showSystemMainIcon();
        }

    }


    /**
     * 直接显示工具面板页面
     */
    static void showToolPanel() {
        DokitIntent dokitViewIntent = new DokitIntent(ToolPanelDokitView.class);
        dokitViewIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
        DokitViewManager.getInstance().attach(dokitViewIntent);
    }


    static void hide() {
        DokitConstant.MAIN_ICON_HAS_SHOW = false;
        DokitConstant.AWAYS_SHOW_MAIN_ICON = false;
        DokitViewManager.getInstance().detach(MainIconDokitView.class.getSimpleName());

    }

    /**
     * 禁用app信息上传开关，该上传信息只为做DoKit接入量的统计，如果用户需要保护app隐私，可调用该方法进行禁用
     */
    static void disableUpload() {
        sEnableUpload = false;
    }

    static boolean isShow() {
        return DokitConstant.MAIN_ICON_HAS_SHOW;
    }


}
