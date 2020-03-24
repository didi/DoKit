//package com.didichuxing.doraemondemo;
//
//import android.content.Context;
//import android.support.annotation.Keep;
//import android.util.Log;
//
//import com.taobao.sophix.PatchStatus;
//import com.taobao.sophix.SophixApplication;
//import com.taobao.sophix.SophixEntry;
//import com.taobao.sophix.SophixManager;
//import com.taobao.sophix.listener.PatchLoadStatusListener;
//
///**
// * ================================================
// * 作    者：jint（金台）
// * 版    本：1.0
// * 创建日期：2020/3/17-16:46
// * 描    述：
// * 修订历史：
// * ================================================
// */
//public class SophixStubApplication extends SophixApplication {
//    private final String TAG = "SophixStubApplication";
//
//    // 此处SophixEntry应指定真正的Application，并且保证RealApplicationStub类名不被混淆。
//    @Keep
//    @SophixEntry(App.class)
//    static class RealApplicationStub {
//    }
//
//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
////         如果需要使用MultiDex，需要在此处调用。
////         MultiDex.install(this);
//        initSophix();
//    }
//
//    private void initSophix() {
//        String appVersion = "0.0.0";
//        try {
//            appVersion = this.getPackageManager()
//                    .getPackageInfo(this.getPackageName(), 0)
//                    .versionName;
//        } catch (Exception e) {
//        }
//        final SophixManager instance = SophixManager.getInstance();
//        instance.setContext(this)
//                .setAppVersion(appVersion)
//                .setSecretMetaData(null, null, null)
//                .setEnableDebug(true)
//                .setEnableFullLog()
//                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
//                    @Override
//                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
//                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {
//                            Log.i(TAG, "sophix load patch success!");
//                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
//                            // 如果需要在后台重启，建议此处用SharePreference保存状态。
//                            Log.i(TAG, "sophix preload patch success. restart app to make effect.");
//                        }
//                    }
//                }).initialize();
//    }
//
//}
