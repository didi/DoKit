package com.didichuxing.doraemondemo;

import android.util.Log;

import com.didichuxing.doraemonkit.aop.DokitPluginConfig;
import com.didichuxing.doraemonkit.aop.urlconnection.HttpUrlConnectionProxyUtil;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/22-11:38
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class AopTest {
    private static final String TAG = "AopTest";

    public void test() {
        try {
            URL url = new URL("sss");
            URLConnection connection = url.openConnection();
            connection.setReadTimeout(1000);
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
