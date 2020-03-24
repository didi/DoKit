package com.didichuxing.doraemonkit.datapick;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.PathUtils;
import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.okgo.DokitOkGo;
import com.didichuxing.doraemonkit.okgo.callback.StringCallback;
import com.didichuxing.doraemonkit.okgo.model.Response;
import com.didichuxing.doraemonkit.util.LogHelper;

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

    /**
     * 添加埋点数据
     *
     * @param eventName
     */
    public void addData(@NonNull String eventName) {

        DataPickBean.EventBean eventBean = new DataPickBean.EventBean(eventName);
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
        //先检查本地是否存在缓存数据
        String strJson = FileIOUtils.readFile2String(filePath);
        if (!TextUtils.isEmpty(strJson)) {
            //上传数据
            realPost(jsonFromFile, strJson);
            return;
        }
        //判断对象是否为null
        if (events == null || events.size() == 0) {
            return;
        }
        dataPickBean.setEvents(events);
        strJson = GsonUtils.toJson(dataPickBean);
        realPost(jsonFromMemory, strJson);

        //ToastUtils.showShort("上传埋点成功");
    }

    /**
     * 真正需要上传的方法
     */
    private void realPost(final int from, String content) {
        //LogHelper.i(TAG,"content===>" + content);
        //LogHelper.i(TAG, "====realPost======from==>" + from);
        DokitOkGo.<String>post(NetworkManager.APP_DATA_PICK_URL)
                .upJson(content)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        //LogHelper.e(TAG, "success===>" + response.body());
                        if (from == jsonFromFile) {
                            FileUtils.delete(filePath);
                        }
                        if (from == jsonFromMemory) {
                            events.clear();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        LogHelper.e(TAG, "error===>" + response.getException().getMessage());
                        //ToastUtils.showShort("上传埋点失败");
                    }
                });

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
