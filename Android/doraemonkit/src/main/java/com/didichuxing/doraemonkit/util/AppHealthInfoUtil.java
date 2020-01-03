package com.didichuxing.doraemonkit.util;

import android.widget.SimpleCursorTreeAdapter;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.JsonUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.didichuxing.doraemonkit.BuildConfig;
import com.didichuxing.doraemonkit.model.AppHealthInfo;
import com.didichuxing.doraemonkit.okgo.OkGo;
import com.didichuxing.doraemonkit.okgo.callback.StringCallback;
import com.didichuxing.doraemonkit.okgo.model.Response;
import com.didichuxing.doraemonkit.okgo.request.PatchRequest;
import com.didichuxing.doraemonkit.ui.base.DokitViewManager;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020-01-02-16:42
 * 描    述：app 健康体检工具类
 * 修订历史：
 * ================================================
 */
public class AppHealthInfoUtil {
    private static String TAG = "AppHealthInfoUtil";

    private AppHealthInfo mAppHealthInfo = new AppHealthInfo();

    /**
     * 静态内部类单例
     */
    private static class Holder {
        private static AppHealthInfoUtil INSTANCE = new AppHealthInfoUtil();
    }

    public static AppHealthInfoUtil getInstance() {
        return AppHealthInfoUtil.Holder.INSTANCE;
    }



    /**
     * 设置基本信息
     *
     * @param caseName   用例名称
     * @param testPerson 测试人员名字
     */
    private void setBaseInfo(String caseName, String testPerson) {
        AppHealthInfo.BaseInfoBean baseInfoBean = new AppHealthInfo.BaseInfoBean();
        baseInfoBean.setTestPerson(testPerson);
        baseInfoBean.setCaseName(caseName);
        baseInfoBean.setAppName(AppUtils.getAppName());
        baseInfoBean.setAppVersion(AppUtils.getAppVersionName());
        baseInfoBean.setDokitVersion(BuildConfig.VERSION_NAME);
        baseInfoBean.setPlatfom("Android");
        baseInfoBean.setPhoneMode(DeviceUtils.getModel());
        baseInfoBean.setTime(TimeUtils.getNowString());
        baseInfoBean.setSystemVersion(DeviceUtils.getSDKVersionName());
        mAppHealthInfo.setBaseInfo(baseInfoBean);
    }

    /**
     * 上传健康体检数据到服务器
     */
    private void post() {
        if (mAppHealthInfo == null) {
            return;
        }
        OkGo.<String>post("")
                .upJson(GsonUtils.toJson(mAppHealthInfo))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LogHelper.i(TAG, "===success===>" + response);
                    }
                });

    }


}
