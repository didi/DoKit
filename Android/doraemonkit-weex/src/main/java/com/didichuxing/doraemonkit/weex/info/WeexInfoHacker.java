package com.didichuxing.doraemonkit.weex.info;


import org.apache.weex.WXEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author haojianglong
 * @date 2019-06-25
 */
public class WeexInfoHacker {

    public static List<WeexInfo> getWeexInfos() {
        List<WeexInfo> infos = new ArrayList<>();
        Set<Map.Entry<String, String>> entrySet = WXEnvironment.getConfig().entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            infos.add(new WeexInfo(entry.getKey(), entry.getValue()));
        }
        return infos;
    }

}
