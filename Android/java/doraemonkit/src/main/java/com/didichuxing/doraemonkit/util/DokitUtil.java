package com.didichuxing.doraemonkit.util;


import android.content.res.Resources;

import android.support.annotation.StringRes;

import com.didichuxing.doraemonkit.DoraemonKit;

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
    public static String param2Json(String param) {
        param = param.replaceAll("=", "\":\"");
        param = param.replaceAll("&", "\",\"");
        return "{\"" + param + "\"}";
    }
}
