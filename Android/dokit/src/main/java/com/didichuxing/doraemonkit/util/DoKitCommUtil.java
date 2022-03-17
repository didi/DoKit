package com.didichuxing.doraemonkit.util;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;

import androidx.annotation.StringRes;

import com.didichuxing.doraemonkit.DoKitEnv;

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
public class DoKitCommUtil {

    public static String getString(@StringRes int stringId) {
        return DoKitEnv.requireApp().getString(stringId);
    }

    @StringRes
    public static int getStringId(String str) {
        try {
            Resources r = DoKitEnv.requireApp().getResources();
            return r.getIdentifier(str, "string", DoKitEnv.requireApp().getPackageName());
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
     * 键值对字符串转json
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
    public static void changeAppOnForeground(Class<? extends Activity> clazz) {
        Intent intent = new Intent(DoKitEnv.requireApp(), clazz);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        DoKitEnv.requireApp().startActivity(intent);
    }
}
