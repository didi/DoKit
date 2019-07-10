package com.didichuxing.doraemonkit.util;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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

    public static void uploadUserInfo(Context context) {
        String url = "https://doraemon.xiaojukeji.com/uploadAppData";
        String appId = SystemUtil.getPackageName(context);
        String appName = SystemUtil.getAppName(context);
        String type = "Android";
        String from = "1";

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("appId", appId);
            jsonObject.put("appName", appName);
            jsonObject.put("version", "1.1.8");
            jsonObject.put("type", type);
            jsonObject.put("from", from);
        } catch (JSONException e) {
            LogHelper.e(TAG, e.toString());
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(mediaType,
                jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                response.close();
            }
        });
    }
}
