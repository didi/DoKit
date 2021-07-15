package com.didichuxing.doraemonkit.util;

import android.content.Context;

import com.didichuxing.doraemonkit.BuildConfig;
import com.didichuxing.doraemonkit.kit.network.NetworkManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by wanglikun on 2018/12/21.
 */
public class DoraemonStatisticsUtil {
    private static final String TAG = "DoraemonStatisticsUtil";

    private DoraemonStatisticsUtil() {
    }

    public static void uploadUserInfo(Context context) throws Exception {
        String appId = DoKitSystemUtil.getPackageName(context);
        String appName = DoKitSystemUtil.getAppName(context);
        String type = "Android";
        //0 代表内部版本  1代表外部版本
        String from = "1";

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("appId", appId);
            jsonObject.put("appName", appName);
            jsonObject.put("appVersion", AppUtils.getAppVersionName());
            jsonObject.put("version", "" + BuildConfig.DOKIT_VERSION);
            jsonObject.put("type", type);
            jsonObject.put("from", from);
            jsonObject.put("language", Locale.getDefault().getDisplayLanguage());
        } catch (JSONException e) {
            LogHelper.e(TAG, e.toString());
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(mediaType,
                jsonObject.toString());
        Request request = new Request.Builder()
                .url(NetworkManager.APP_START_DATA_PICK_URL)
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                response.close();
            }
        });
    }
}
