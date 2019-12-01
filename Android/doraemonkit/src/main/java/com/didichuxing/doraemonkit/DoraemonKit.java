package com.didichuxing.doraemonkit;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.SparseArray;
import android.widget.Toast;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.didichuxing.doraemonkit.config.PerformanceSpInfoConfig;
import com.didichuxing.doraemonkit.constant.SharedPrefsKey;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.kit.IKit;
import com.didichuxing.doraemonkit.kit.alignruler.AlignRuler;
import com.didichuxing.doraemonkit.kit.blockmonitor.BlockMonitorKit;
import com.didichuxing.doraemonkit.kit.colorpick.ColorPicker;
import com.didichuxing.doraemonkit.kit.crash.CrashCapture;
import com.didichuxing.doraemonkit.kit.custom.Custom;
import com.didichuxing.doraemonkit.kit.dataclean.DataClean;
import com.didichuxing.doraemonkit.kit.dbdebug.DbDebugKit;
import com.didichuxing.doraemonkit.kit.fileexplorer.FileExplorer;
import com.didichuxing.doraemonkit.kit.gpsmock.GpsMock;
import com.didichuxing.doraemonkit.kit.gpsmock.GpsMockManager;
import com.didichuxing.doraemonkit.kit.gpsmock.ServiceHookManager;
import com.didichuxing.doraemonkit.kit.largepicture.LargePictureKit;
import com.didichuxing.doraemonkit.kit.layoutborder.LayoutBorder;
import com.didichuxing.doraemonkit.kit.logInfo.LogInfo;
import com.didichuxing.doraemonkit.kit.methodtrace.MethodCostKit;
import com.didichuxing.doraemonkit.kit.mode.FloatModeKit;
import com.didichuxing.doraemonkit.kit.network.NetworkKit;
import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.kit.parameter.cpu.Cpu;
import com.didichuxing.doraemonkit.kit.parameter.frameInfo.FrameInfo;
import com.didichuxing.doraemonkit.kit.parameter.ram.Ram;
import com.didichuxing.doraemonkit.kit.sysinfo.SysInfo;
import com.didichuxing.doraemonkit.kit.temporaryclose.TemporaryClose;
import com.didichuxing.doraemonkit.kit.timecounter.TimeCounterKit;
import com.didichuxing.doraemonkit.kit.timecounter.instrumentation.HandlerHooker;
import com.didichuxing.doraemonkit.kit.uiperformance.UIPerformance;
import com.didichuxing.doraemonkit.kit.version.DokitVersion;
import com.didichuxing.doraemonkit.kit.viewcheck.ViewChecker;
import com.didichuxing.doraemonkit.kit.weaknetwork.WeakNetwork;
import com.didichuxing.doraemonkit.kit.webdoor.WebDoor;
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorManager;
import com.didichuxing.doraemonkit.ui.UniversalActivity;
import com.didichuxing.doraemonkit.ui.base.AbsDokitView;
import com.didichuxing.doraemonkit.ui.base.DokitIntent;
import com.didichuxing.doraemonkit.ui.base.DokitViewManager;
import com.didichuxing.doraemonkit.ui.kit.KitItem;
import com.didichuxing.doraemonkit.ui.main.FloatIconDokitView;
import com.didichuxing.doraemonkit.util.DoraemonStatisticsUtil;
import com.didichuxing.doraemonkit.util.LogHelper;
import com.didichuxing.doraemonkit.util.PermissionUtil;
import com.didichuxing.doraemonkit.util.SharedPrefsUtil;
import com.didichuxing.doraemonkit.util.UIUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangweida on 2018/6/22.
 */

public class DoraemonKit {
    private static final String TAG = "DoraemonKit";

    private static SparseArray<List<IKit>> sKitMap = new SparseArray<>();

    private static List<LifecycleListener> sListeners = new ArrayList<>();

    private static boolean sHasRequestPermission;

    private static boolean sHasInit = false;
    /**
     * 用来判断系统悬浮窗的入口浮标是否显示
     */
    private static boolean mSystemDokitViewIcon = true;
    /**
     * 是否允许上传统计信息
     */
    private static boolean sEnableUpload = true;
    /**
     * 是否是普通的浮标模式
     */
    public static boolean IS_NORMAL_FLOAT_MODE = true;

    public static Application APPLICATION;

    /**
     * fragment 生命周期回调
     */
    private static FragmentManager.FragmentLifecycleCallbacks sFragmentLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {
        @Override
        public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
            super.onFragmentAttached(fm, f, context);
            LogHelper.d(TAG, "onFragmentAttached: " + f);
            for (LifecycleListener listener : sListeners) {
                listener.onFragmentAttached(f);
            }
        }

        @Override
        public void onFragmentDetached(FragmentManager fm, Fragment f) {
            super.onFragmentDetached(fm, f);
            LogHelper.d(TAG, "onFragmentDetached: " + f);
            for (LifecycleListener listener : sListeners) {
                listener.onFragmentDetached(f);
            }
        }
    };

    public static void setDebug(boolean debug) {
        LogHelper.setDebug(debug);
    }

    public static void install(final Application app) {
        install(app, null);
    }

    public static void setWebDoorCallback(WebDoorManager.WebDoorCallback callback) {
        WebDoorManager.getInstance().setWebDoorCallback(callback);
    }

    public static void install(final Application app, List<IKit> selfKits) {
        //添加常用工具
        if (sHasInit) {
            //已经初始化添加自定义kits
            if (selfKits != null) {
                List<IKit> biz = sKitMap.get(Category.BIZ);
                if (biz != null) {
                    biz.clear();
                    biz.addAll(selfKits);
                    for (IKit kit : biz) {
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
        String StrfloatMode = SharedPrefsUtil.getString(app, SharedPrefsKey.FLOAT_START_MODE, "normal");
        if (StrfloatMode.equals("normal")) {
            IS_NORMAL_FLOAT_MODE = true;
        } else {
            IS_NORMAL_FLOAT_MODE = false;
        }

        //解锁系统隐藏api限制权限以及hook Instrumentation
        HandlerHooker.doHook(app);
        //hook WIFI GPS Telephony系统服务
        ServiceHookManager.getInstance().install(app);
        //注册全局的activity生命周期回调
        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            int startedActivityCounts;

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                if (ignoreCurrentActivityDokitView(activity)) {
                    return;
                }
                if (activity instanceof FragmentActivity) {
                    //注册fragment生命周期回调
                    ((FragmentActivity) activity).getSupportFragmentManager().registerFragmentLifecycleCallbacks(sFragmentLifecycleCallbacks, true);
                }

            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (ignoreCurrentActivityDokitView(activity)) {
                    return;
                }
                if (startedActivityCounts == 0) {
                    DokitViewManager.getInstance().notifyForeground();

                }
                startedActivityCounts++;
            }

            /**
             * 当activity进入可交互状态
             * @param activity
             */
            @Override
            public void onActivityResumed(Activity activity) {
                //如果是leakCanary页面不进行添加
                if (ignoreCurrentActivityDokitView(activity)) {
                    return;
                }
                //设置app的直接子view的Id
                if (UIUtils.getDokitAppContentView(activity) != null) {
                    UIUtils.getDokitAppContentView(activity).setId(R.id.dokit_app_contentview_id);
                }


                if (IS_NORMAL_FLOAT_MODE) {
                    //显示内置popView icon
                    resumeAndAttachDokitViews(activity);
                } else {
                    //悬浮窗权限 vivo 华为可以不需要动态权限 小米需要
                    if (PermissionUtil.canDrawOverlays(activity)) {
                        //系统悬浮窗需要判断浮标是否已经显示
                        if (mSystemDokitViewIcon) {
                            showSystemMainIcon();
                        }
                        systemDokitViewOnResume(activity);
                    } else {
                        //请求悬浮窗权限
                        requestPermission(activity);
                    }
                }

                for (LifecycleListener listener : sListeners) {
                    listener.onActivityResumed(activity);
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
                if (ignoreCurrentActivityDokitView(activity)) {
                    return;
                }
                for (LifecycleListener listener : sListeners) {
                    listener.onActivityPaused(activity);
                }

                //sCurrentResumedActivity = null;
            }

            @Override
            public void onActivityStopped(Activity activity) {
                if (ignoreCurrentActivityDokitView(activity)) {
                    return;
                }
                startedActivityCounts--;
                //通知app退出到后台
                if (startedActivityCounts == 0) {
                    DokitViewManager.getInstance().notifyBackground();

                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                if (ignoreCurrentActivityDokitView(activity)) {
                    return;
                }
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (ignoreCurrentActivityDokitView(activity)) {
                    return;
                }
                //注销fragment的生命周期回调
                if (activity instanceof FragmentActivity) {
                    ((FragmentActivity) activity).getSupportFragmentManager().unregisterFragmentLifecycleCallbacks(sFragmentLifecycleCallbacks);
                }
                DokitViewManager.getInstance().onActivityDestroy(activity);
            }
        });
        sKitMap.clear();
        //业务专区
        List<IKit> biz = new ArrayList<>();
        //weex专区
        List<IKit> weex = new ArrayList<>();

        //常用工具
        List<IKit> tool = new ArrayList<>();
        //性能监控
        List<IKit> performance = new ArrayList<>();
        //视觉工具
        List<IKit> ui = new ArrayList<>();
        //悬浮窗模式
        List<IKit> floatMode = new ArrayList<>();
        //退出
        List<IKit> exit = new ArrayList<>();
        //版本号
        List<IKit> version = new ArrayList<>();
        //添加工具kit
        tool.add(new SysInfo());
        tool.add(new FileExplorer());
        if (GpsMockManager.getInstance().isMockEnable()) {
            tool.add(new GpsMock());
        }
        tool.add(new WebDoor());
        tool.add(new CrashCapture());
        tool.add(new LogInfo());
        tool.add(new DataClean());
        tool.add(new WeakNetwork());
        tool.add(new DbDebugKit());

        //添加性能监控kit
        performance.add(new FrameInfo());
        performance.add(new Cpu());
        performance.add(new Ram());
        performance.add(new NetworkKit());
        performance.add(new BlockMonitorKit());
        performance.add(new TimeCounterKit());
        performance.add(new MethodCostKit());
        performance.add(new UIPerformance());
        performance.add(new LargePictureKit());
        try {
            //动态添加leakcanary
            IKit leakCanaryKit = (IKit) Class.forName("com.didichuxing.doraemonkit.kit.leakcanary.LeakCanaryKit").newInstance();
            performance.add(leakCanaryKit);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        performance.add(new Custom());

        //添加视觉ui kit
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ui.add(new ColorPicker());
        }

        ui.add(new AlignRuler());
        ui.add(new ViewChecker());
        ui.add(new LayoutBorder());
        //增加浮标模式
        floatMode.add(new FloatModeKit());
        //添加退出项
        exit.add(new TemporaryClose());
        //添加版本号项
        version.add(new DokitVersion());
        //添加自定义
        if (selfKits != null && !selfKits.isEmpty()) {
            biz.addAll(selfKits);
        }
        //调用kit 初始化
        for (IKit kit : biz) {
            kit.onAppInit(app);
        }
        for (IKit kit : performance) {
            kit.onAppInit(app);
        }
        for (IKit kit : tool) {
            kit.onAppInit(app);
        }
        for (IKit kit : ui) {
            kit.onAppInit(app);
        }
        //注入到sKitMap中
        sKitMap.put(Category.BIZ, biz);
        //动态添加weex专区
        try {
            IKit weexLogKit = (IKit) Class.forName("com.didichuxing.doraemonkit.weex.log.WeexLogKit").newInstance();
            weex.add(weexLogKit);
            IKit storageKit = (IKit) Class.forName("com.didichuxing.doraemonkit.weex.storage.StorageKit").newInstance();
            weex.add(storageKit);
            IKit weexInfoKit = (IKit) Class.forName("com.didichuxing.doraemonkit.weex.info.WeexInfoKit").newInstance();
            weex.add(weexInfoKit);
            IKit devToolKit = (IKit) Class.forName("com.didichuxing.doraemonkit.weex.devtool.DevToolKit").newInstance();
            weex.add(devToolKit);
            sKitMap.put(Category.WEEX, weex);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        sKitMap.put(Category.PERFORMANCE, performance);
        sKitMap.put(Category.TOOLS, tool);
        sKitMap.put(Category.UI, ui);
        sKitMap.put(Category.FLOAT_MODE, floatMode);
        sKitMap.put(Category.CLOSE, exit);
        sKitMap.put(Category.VERSION, version);
        //初始化悬浮窗管理类
        DokitViewManager.getInstance().init(app);
        //上传app基本信息便于统计
        if (sEnableUpload) {
            DoraemonStatisticsUtil.uploadUserInfo(app);
        }
        installLeakCanary(app);
        initAndroidUtil(app);
        checkLargeImgIsOpen();
    }

    //确认大图检测功能时候被打开
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
            Method install = leakCanaryManager.getMethod("install", Application.class);
            //调用静态的install方法
            install.invoke(null, app);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
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


    private static void requestPermission(Context context) {
        if (!PermissionUtil.canDrawOverlays(context) && !sHasRequestPermission) {
            Toast.makeText(context, context.getText(R.string.dk_float_permission_toast), Toast.LENGTH_SHORT).show();
            //请求悬浮窗权限
            PermissionUtil.requestDrawOverlays(context);
            sHasRequestPermission = true;
        }
    }

    /**
     * 显示系统悬浮窗icon
     */
    private static void showSystemMainIcon() {
        if (ActivityUtils.getTopActivity() instanceof UniversalActivity) {
            return;
        }

        DokitIntent intent = new DokitIntent(FloatIconDokitView.class);
        intent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
        DokitViewManager.getInstance().attach(intent);
    }

    /**
     * 显示所有应该显示的popView
     *
     * @param activity
     */
    private static void resumeAndAttachDokitViews(Activity activity) {

        DokitViewManager.getInstance().resumeAndAttachDokitViews(activity);
    }

    private static void systemDokitViewOnResume(Activity activity) {
        Map<String, AbsDokitView> dokitViewMap = DokitViewManager.getInstance().getDokitViews(activity);
        for (AbsDokitView absDokitView : dokitViewMap.values()) {
            absDokitView.onResume();
        }
    }

    /**
     * 是否忽略在当前的activity上显示浮标
     *
     * @param activity
     * @return
     */
    private static boolean ignoreCurrentActivityDokitView(Activity activity) {
        String[] ignoreActivityClassNames = new String[]{"DisplayLeakActivity"};
        for (String activityClassName : ignoreActivityClassNames) {
            if (activity.getClass().getSimpleName().equals(activityClassName)) {
                return true;
            }
        }
        return false;
    }


    public static List<IKit> getKitList(int catgory) {
        if (sKitMap.get(catgory) != null) {
            return new ArrayList<>(sKitMap.get(catgory));
        } else {
            return null;
        }
    }

    /**
     * 将指定类目的kits转为指定的KitItems
     *
     * @param catgory
     * @return
     */
    public static List<KitItem> getKitItems(int catgory) {
        if (sKitMap.get(catgory) != null) {
            List<KitItem> kitItems = new ArrayList<>();
            for (IKit kit : sKitMap.get(catgory)) {
                kitItems.add(new KitItem(kit));
            }
            return kitItems;
        } else {
            return null;
        }
    }

    public interface LifecycleListener {
        void onActivityResumed(Activity activity);

        void onActivityPaused(Activity activity);

        void onFragmentAttached(Fragment f);

        void onFragmentDetached(Fragment f);
    }

    /**
     * 悬浮窗初始化时注册activity以及fragment生命周期回调监听
     *
     * @param listener
     */
    public static void registerListener(LifecycleListener listener) {
        sListeners.add(listener);
    }

    /**
     * 悬浮窗关闭时注销监听
     *
     * @param listener
     */
    public static void unRegisterListener(LifecycleListener listener) {
        sListeners.remove(listener);
    }

    public static void show() {
        if (!isShow()) {
            showSystemMainIcon();
        }
        mSystemDokitViewIcon = true;
    }

    public static void hide() {
        DokitViewManager.getInstance().detach(FloatIconDokitView.class.getSimpleName());

        mSystemDokitViewIcon = false;
    }

    /**
     * 禁用app信息上传开关，该上传信息只为做DoKit接入量的统计，如果用户需要保护app隐私，可调用该方法进行禁用
     */
    public static void disableUpload() {
        sEnableUpload = false;
    }

    public static boolean isShow() {
        return mSystemDokitViewIcon;
    }

    public static Activity getCurrentResumedActivity() {
        return ActivityUtils.getTopActivity();
    }

    public static void enableRequestPermissionSelf() {
        sHasRequestPermission = true;
    }
}
