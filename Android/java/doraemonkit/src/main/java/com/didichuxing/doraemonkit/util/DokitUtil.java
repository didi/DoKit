package com.didichuxing.doraemonkit.util;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;

import androidx.annotation.StringRes;

import com.didichuxing.doraemonkit.DoraemonKit;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.RequestBody;
import okio.Buffer;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/3/27-15:08
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class DokitUtil {

    public static String getString(@StringRes int stringId) {
        return DoraemonKit.APPLICATION.getString(stringId);
    }

    @StringRes
    public static int getStringId(String str) {
        try {
            Resources r = DoraemonKit.APPLICATION.getResources();
            return r.getIdentifier(str, "string", DoraemonKit.APPLICATION.getPackageName());
        } catch (Exception e) {
            LogHelper.e("getStringId", "getStringId===>" + str);
        }
        return -1;
    }


    public static String requestBodyToString(RequestBody requestBody) {
        try {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            return buffer.readUtf8();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 字符串对转json
     *
     * @param param
     * @return
     */
    public static String param2Json(String param) throws JSONException {
        String[] params = param.split("&");
        JSONObject jsonObject = new JSONObject();

        for (String p : params) {
            String[] ps = p.split("=");
            if (ps.length == 2) {
                String key = ps[0];
                String value = ps[1];
                jsonObject.put(key, value);
            }
        }

        return jsonObject.toString();
    }

    /**
     * 切换App到前台
     */
    public static void changeAppOnForeground(Class<Activity> clazz) {
        Intent intent = new Intent(DoraemonKit.APPLICATION, clazz);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        DoraemonKit.APPLICATION.startActivity(intent);
    }
}
