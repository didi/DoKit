package com.didichuxing.doraemonkit.aop;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/27-18:24
 * 描    述：app三方库信息
 * 修订历史：
 * ================================================
 */
public class DokitThirdLibInfo {
    private static final String TAG = "DokitThirdLibInfo";
    public static Map<String, String> THIRD_LIB_INFOS = new HashMap<>();
    /**
     * key===>groupId:artifactId
     */
    public static Map<String, String> THIRD_LIB_INFOS_SIMPLE = new HashMap<>();


    /**
     * 注入插件配置 动态注入到DoraemonKitReal#pluginConfig方法中
     */
    public static void inject(Map config) {
        //LogHelper.i(TAG, "map====>" + config);
        THIRD_LIB_INFOS = config;
        THIRD_LIB_INFOS_SIMPLE.clear();
        Iterator entries = THIRD_LIB_INFOS.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            String key = (String) entry.getKey();
            String[] keys = key.split(":");
            if (keys.length == 3) {
                String groupId = keys[0];
                String artifactId = keys[1];
                String newKey = groupId + ":" + artifactId;
                String value = (String) entry.getValue();
                THIRD_LIB_INFOS_SIMPLE.put(newKey, value);
            }

        }
    }
}
