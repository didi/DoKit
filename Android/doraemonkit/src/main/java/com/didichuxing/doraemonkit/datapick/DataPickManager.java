package com.didichuxing.doraemonkit.datapick;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.DokitConstant;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.okgo.OkGo;
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
    private List<DataPickBean> dataPicks = new ArrayList<>();

    private static class Holder {
        private static DataPickManager INSTANCE = new DataPickManager();
    }

    public static DataPickManager getInstance() {
        return DataPickManager.Holder.INSTANCE;
    }

    /**
     * 添加埋点数据
     *
     * @param eventType
     * @param name
     */
    public void addData(@NonNull String eventType, int kitGroup, @NonNull String name) {
        String strGroup = "main";
        switch (kitGroup) {
            case Category.BIZ:
                strGroup = DoraemonKit.APPLICATION.getString(R.string.dk_category_biz);
                break;

            case Category.WEEX:
                strGroup = DoraemonKit.APPLICATION.getString(R.string.dk_category_weex);
                break;
            case Category.PERFORMANCE:
                strGroup = DoraemonKit.APPLICATION.getString(R.string.dk_category_performance);
                break;

            case Category.TOOLS:
                strGroup = DoraemonKit.APPLICATION.getString(R.string.dk_category_tools);
                break;

            case Category.UI:
                strGroup = DoraemonKit.APPLICATION.getString(R.string.dk_category_ui);
                break;
            case Category.PLATFORM:
                strGroup = DoraemonKit.APPLICATION.getString(R.string.dk_category_platform);

                break;
            default:
                strGroup = "main";
                break;
        }


        DataPickBean dataPickBean = new DataPickBean(eventType, strGroup, name);
        if (dataPicks != null) {
            dataPicks.add(dataPickBean);
            //链表数据大于10s 上传数据
            if (dataPicks.size() >= 10) {
                postData();
                return;
            }
            //两个埋点之间的时间大于等于60s上传数据
            if (dataPicks.size() >= 2) {
                long lastTime = dataPicks.get(dataPicks.size() - 1).getTime();
                long lastSecondTime = dataPicks.get(dataPicks.size() - 2).getTime();
                if (lastTime - lastSecondTime >= 60 * 1000) {
                    postData();
                }
            }

        } else {
            dataPicks = new ArrayList<>();
            dataPicks.add(dataPickBean);
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
        if (dataPicks == null || dataPicks.size() == 0) {
            return;
        }

        strJson = GsonUtils.toJson(dataPicks);
        realPost(jsonFromMemory, strJson);

        //ToastUtils.showShort("上传埋点成功");
    }

    /**
     * 真正需要上传的方法
     */
    private void realPost(final int from, String content) {
        LogHelper.i(TAG, "====realPost======");
        OkGo.<String>post("")
                .params("productId", DokitConstant.PRODUCT_ID)
                .params("content", content)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (from == jsonFromFile) {
                            FileUtils.delete(filePath);
                        }
                        if (from == jsonFromMemory) {
                            dataPicks.clear();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        ToastUtils.showShort("上传埋点失败");
                    }
                });

    }

    private String filePath = PathUtils.getInternalAppFilesPath() + File.separator + "dokit.json";

    /**
     * 异常情况下保存到本地保存到本地
     */
    public void saveData2Local() {
        if (dataPicks == null || dataPicks.size() == 0) {
            return;
        }
        //保存数据到本地
        FileIOUtils.writeFileFromString(filePath, GsonUtils.toJson(dataPicks));
    }
}
