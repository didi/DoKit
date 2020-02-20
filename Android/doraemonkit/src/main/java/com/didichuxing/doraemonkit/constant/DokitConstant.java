package com.didichuxing.doraemonkit.constant;

import android.util.SparseArray;

import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.config.GlobalConfig;
import com.didichuxing.doraemonkit.kit.IKit;
import com.didichuxing.doraemonkit.kit.dbdebug.DbDebugFragment;
import com.didichuxing.doraemonkit.kit.network.room_db.DokitDbManager;
import com.didichuxing.doraemonkit.model.ActivityLifecycleInfo;
import com.didichuxing.doraemonkit.ui.kit.KitItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-19-10:21
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class DokitConstant {
    public static boolean IS_HOOK = false;

    /**
     * 产品id
     */
    public static String PRODUCT_ID = "";

    /**
     * 是否处于健康体检中
     */
    public static boolean APP_HEALTH_RUNNING = GlobalConfig.getAppHealth(DoraemonKit.APPLICATION);

    /**
     * 是否是普通的浮标模式
     */
    public static boolean IS_NORMAL_FLOAT_MODE = true;

    /**
     * 是否显示icon主入口
     */
    public static boolean AWAYS_SHOW_MAIN_ICON = true;

    /**
     * icon主入口是否处于显示状态
     */
    public static boolean MAIN_ICON_HAS_SHOW = false;


    /**
     * 全局DBDebugFragment
     */
    public static WeakReference<DbDebugFragment> DB_DEBUG_FRAGMENT;

    /**
     * 全局的dokit Kit信息
     */
    public static SparseArray<List<IKit>> KIT_MAPS = new SparseArray<>();

    public static Map<String, ActivityLifecycleInfo> ACTIVITY_LIFECYCLE_INFOS = new HashMap<>();

    public static List<IKit> getKitList(int catgory) {
        if (KIT_MAPS.get(catgory) != null) {
            return new ArrayList<>(KIT_MAPS.get(catgory));
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
        if (KIT_MAPS.get(catgory) != null) {
            List<KitItem> kitItems = new ArrayList<>();
            for (IKit kit : KIT_MAPS.get(catgory)) {
                kitItems.add(new KitItem(kit));
            }
            return kitItems;
        } else {
            return null;
        }
    }

    /**
     * 判断接入的是否是滴滴内部的rpc sdk
     *
     * @return
     */
    public static boolean isRpcSDK() {
        boolean isRpcSdk;
        try {
            Class.forName("com.didichuxing.doraemonkit.DoraemonKitRpc");
            isRpcSdk = true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            isRpcSdk = false;
        }

        return isRpcSdk;
    }


    /**
     * 兼容滴滴内部外网映射环境  该环境的 path上会多一级/kop_xxx/路径
     *
     * @param oldPath
     * @param fromSDK
     * @return
     */
    public static String dealDidiPlatformPath(String oldPath, int fromSDK) {
        if (fromSDK == DokitDbManager.FROM_SDK_OTHER) {
            return oldPath;
        }
        String newPath = oldPath;
        //包含多级路径
        if (oldPath.contains("/kop") && oldPath.split("\\/").length > 1) {
            //比如/kop_stable/a/b/gateway 分解以后为 "" "kop_stable" "a" "b" "gateway"
            String[] childPaths = oldPath.split("\\/");
            String firstPath = childPaths[1];
            if (firstPath.contains("kop")) {
                newPath = oldPath.replace("/" + firstPath, "");
            }
        }
        return newPath;
    }

}
