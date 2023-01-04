package com.didichuxing.doraemonkit.datapick;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.didichuxing.doraemonkit.util.FileIOUtils;
import com.didichuxing.doraemonkit.util.FileUtils;
import com.didichuxing.doraemonkit.util.GsonUtils;
import com.didichuxing.doraemonkit.util.PathUtils;
import com.didichuxing.doraemonkit.kit.core.DoKitManager;
import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.util.LogHelper;
import com.didichuxing.doraemonkit.volley.VolleyManager;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020-02-19-13:27
 * 描    述：dokit 埋点管理类
 * 修订历史：
 * ================================================
 */
public class DataPickManager {
    private static final String TAG = "DataPickManager";
    /**
     * 埋点集合
     */
    private List<DataPickBean.EventBean> events = new ArrayList<>();

    private DataPickBean dataPickBean = new DataPickBean();

    private static class Holder {
        private static DataPickManager INSTANCE = new DataPickManager();
    }

    public static DataPickManager getInstance() {
        return DataPickManager.Holder.INSTANCE;
    }


    public void addData(@NonNull String eventName) {
        addData(eventName, "");
    }

    public void addData(@NonNull String eventName, @NonNull String pageId) {
        addData(eventName, pageId,"");
    }

    /**
     * 添加埋点数据
     *
     * @param eventName
     */
    public void addData(@NonNull String eventName, @NonNull String pageId,String businessName) {

        DataPickBean.EventBean eventBean = new DataPickBean.EventBean(eventName, pageId,businessName);
        if (events != null) {
            events.add(eventBean);
            //链表数据大于10s 上传数据
            if (events.size() >= 10) {
                postData();
                return;
            }
            //两个埋点之间的时间大于等于60s上传数据
            if (events.size() >= 2) {
                long lastTime = Long.parseLong(events.get(events.size() - 1).getTime());
                long lastSecondTime = Long.parseLong(events.get(events.size() - 2).getTime());
                if (lastTime - lastSecondTime >= 60 * 1000) {
                    postData();
                }
            }

        } else {
            events = new ArrayList<>();
            events.add(eventBean);
        }
    }

    private static int jsonFromFile = 100;

    private static int jsonFromMemory = 101;

    /**
     * 上传埋点数据
     */
    public void postData() {
        if (!DoKitManager.INSTANCE.getENABLE_UPLOAD()) {
            return;
        }

        //先检查本地是否存在缓存数据
        String strJson = FileIOUtils.readFile2String(filePath);
        if (!TextUtils.isEmpty(strJson)) {
            //上传数据
            try {
                realPost(jsonFromFile, strJson);
            } catch (Exception e) {
                //e.printStackTrace();
            }
            return;
        }
        //判断对象是否为null
        if (events == null || events.size() == 0) {
            return;
        }
        dataPickBean.setEvents(events);
        strJson = GsonUtils.toJson(dataPickBean);
        try {
            realPost(jsonFromMemory, strJson);
        } catch (Exception e) {
            //e.printStackTrace();
        }

    }

    /**
     * 真正需要上传的方法
     */
    private void realPost(final int from, String content) throws Exception {

        //LogHelper.i(TAG,"content===>" + content);
        //LogHelper.i(TAG, "====realPost======from==>" + from);
        Request requset = new JsonObjectRequest(Request.Method.POST, NetworkManager.APP_DATA_PICK_URL, new JSONObject(content), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
//                LogHelper.e(TAG, "success===>" + response.toString());
                if (from == jsonFromFile) {
                    FileUtils.delete(filePath);
                }
                if (from == jsonFromMemory) {
                    events.clear();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.e(TAG, "error===>" + error.getMessage());
                //ToastUtils.showShort("上传埋点失败");
            }
        });

        VolleyManager.INSTANCE.add(requset);
    }

    private String filePath = PathUtils.getInternalAppFilesPath() + File.separator + "dokit.json";

    /**
     * 异常情况下保存到本地保存到本地
     */
    public void saveData2Local() {
        if (events == null || events.size() == 0) {
            return;
        }
        dataPickBean.setEvents(events);
        //保存数据到本地
        FileIOUtils.writeFileFromString(filePath, GsonUtils.toJson(dataPickBean));
    }
}
